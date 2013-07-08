#!/usr/bin/python
#-*- encoding: utf-8 -*-

import logging
import os
import tempfile
import errno
import argparse
import json
import sys
from string import Template
from multiprocessing import Pool
import subprocess


import logging.config
import lxml.etree as ET
from colorlog import ColoredFormatter

from path import path


def setup_logging():
    formatter = ColoredFormatter(
        "%(log_color)s%(levelname)-8s%(reset)s %(blue)s%(message)s",
        datefmt=None,
        reset=True,
        log_colors={
            'DEBUG': 'cyan',
            'INFO': 'green',
            'WARNING': 'yellow',
            'ERROR': 'red',
            'CRITICAL': 'red',
        }
    )

    c = logging.getLogger("console")
    c.setLevel(logging.DEBUG)
    clr = logging.StreamHandler(sys.stderr)
    clr.setFormatter(formatter)
    c.addHandler(clr)


    #p = logging.getLogger("program")
    #p.setLevel(logging.INFO)
    #p.addHandler(logging.FileHandler("output.txt"))

    return c,c

console_logger, program_logger = setup_logging()

COMMAND_EXTRACT_AUDIO = Template(
    "ffmpeg -i ${src} -vn -ar 16000 -ss ${start} -t ${duration} -f flac ${out}.flac")
COMMAND_EXTRACT_VIDEO = Template("ffmpeg -y -i ${src} -r ${framerate}  -f image2 ${dir}/%07d.png")
COMMAND_TESSERACT = Template("tesseract ${input} ${out} -l ${lang} -psm ${psm}")
COMMAND_EXTRACT_DURATION = Template("ffmpeg -i ${src}")


def execute_with_log(command):
    console_logger.info("EXECUTE: %s", command)
    with open("output.txt", "a") as out:
        return subprocess.call(command, stdout=out, stderr=out, shell=True)


def mkdir_p(path):
    try:
        os.makedirs(path)
    except OSError as exc: # Python >2.5
        if exc.errno == errno.EEXIST and os.path.isdir(path):
            pass
        else:
            raise


def get_duration(input_file):
    s = COMMAND_EXTRACT_DURATION.substitute({'src': input_file})
    cout, cin, cerr = os.popen3(s)

    import re

    pattern = re.compile(r'Duration: (\d\d:\d\d:\d\d.\d\d)')

    tim = None
    for line in cerr.readlines():
        line = line.strip()
        m = pattern.findall(line)
        print line, m
        if m:
            t = m[0]
            tim = int(t[0:2]) * 60 * 60 + int(t[3:5]) * 60 + int(t[6:8])
            console_logger.info("in seconds: %d", tim)
            break

    cout.close()
    cin.close()
    cerr.close()
    return tim


def extract_audio(input_video, target_folder, deltaT):
    length = get_duration(input_video)
    mkdir_p(target_folder)
    pool = Pool(4)

    for t in range(0, length, deltaT):
        p = {'src': input_video, 'out': target_folder + "/" + str(t / deltaT), 'start': t, 'duration': deltaT}
        s = COMMAND_EXTRACT_AUDIO.substitute(p)
        execute_with_log(s)
        #pool.apply_async(os.system, s)

    pool.close()
    #pool.join()


def extract_video(input_video, target_folder, deltaT=5):
    cmd = COMMAND_EXTRACT_VIDEO.substitute(
        {'src': input_video, 'dir': target_folder, 'framerate': 1. / deltaT})
    mkdir_p(target_folder)
    execute_with_log(cmd)


def gather_files(target_folder, filter="*.png", deltaT=5):
    def time(fil):
        name, ext = os.path.splitext(fil)
        pos = int(os.path.basename(name))
        return (pos - 1) * deltaT

    files = [(time(fil), fil) for fil in path(target_folder).listdir(filter)]
    files = sorted(files, key=lambda a: a[0])
    return files


def speech_to_text(audio_file, lang="en-us"):
    fin = os.popen("java SpeechRec %s %s" % (audio_file,lang))
    data = fin.read()
    fin.close()
    if not data:
        return ""
    print data
    jsdata = json.loads(data)
    if jsdata["hypotheses"]:
        return jsdata["hypotheses"][0]["utterance"]
    else:
        return ""


def ocr_image(image, lang="deu", psm=1):
    out = tempfile.mktemp()

    cmd = COMMAND_TESSERACT.substitute(
        {'input': image, 'out': out, 'lang': lang, 'psm': psm})
    console_logger.info(cmd)
    a = execute_with_log(cmd)
    if a == 0:
        with open("%s.txt" % out) as f:
            return f.read()
    raise Exception("error in tesseracting %s" % image)


def ocr(doc):
    time, image = doc[0], doc[1]
    ndoc = ocr_image(image)
    return time, ndoc


def sr(doc):
    time, audio = doc[0], doc[1]
    ndoc = speech_to_text(audio)
    return time, ndoc


def print_timed_document(input_file, deltaT, docs, dest = None):
    def clean(s):
        return filter(lambda c: 32 <= ord(c) <= 126, s)

    root = ET.Element("timed-document", file=input_file)
    for time, content in docs:
        slot = ET.SubElement(root, 'slot', start=str(time), end=str(time + deltaT))
        slot.text = clean(content)

    xml = ET.tostring(root, pretty_print=True, standalone=True, encoding="utf-8")
    if dest:
        with open(dest,'w') as f:
            f.write(xml)
    else:
        print xml



def main(inputfile, video=True, audio=True,
         deltaT=60, audiodir=None, videodir=None, dest=None):
    newAudioDir = audiodir if audiodir else tempfile.mkdtemp()
    newPictureDir = videodir if videodir else tempfile.mkdtemp()

    console_logger.info("use %s for pictures", newPictureDir)
    console_logger.info("use %s for pictures", newAudioDir)


    console_logger.info("VIDEO %s, AUDIO %s, DELTAT %s", video, audio, deltaT)

    pool = Pool(4)

    adocs = vdocs = []
    if audio:
        #extract_audio(inputfile, newAudioDir, deltaT)
        audios = gather_files(newAudioDir, "*flac", deltaT)
        adocs = map(sr, audios)

    if video:
        extract_video(inputfile, newPictureDir, deltaT)
        images = gather_files(newPictureDir, "*.png", deltaT)
        vdocs = pool.map(ocr, images)

    pool.close()

    if audio and not video:
        docs = adocs
    elif video and not audio:
        docs = vdocs
    else:
        docs = []
        for (at, ac), (vt, vc) in zip(adocs, vdocs):
            docs.append((at, ac + vc))

    print_timed_document(inputfile, deltaT, docs , dest)


def get_parser():
    parser = argparse.ArgumentParser(
        epilog="EPILOG",
        description='Process some integers.')

    parser.add_argument('input', type=str, metavar="FILE",
                        help='input file, audio and/or video')

    parser.add_argument('--no-audio', dest="noaudio", action="store_true", default=False,
                        help='input file, audio and/or video')

    parser.add_argument('--no-video', dest="novideo", action="store_true", default=False,
                        help='input file, audio and/or video')

    parser.add_argument('--temp-dir-audio', dest="tempDirAudio", type=str, metavar="FOLDER",
                        help="")

    parser.add_argument('--temp-dir-video', dest="tempDirVideo", type=str, metavar="FOLDER",
                        help="")

    parser.add_argument('--delta', '-d', dest='deltaT', default=60, metavar="SECONDS",
                        help="period time (e.g. take a snapshot ever t seconds")

    parser.add_argument("--output", "-o", dest="destination", default=None, metavar="FILE", help="")
    return parser


if __name__ == "__main__":
    p = get_parser()
    args, input = p.parse_known_args()


    main(inputfile=args.input,
         video=not args.novideo,
         audio=not args.noaudio,
         deltaT=int(args.deltaT),
         audiodir=args.tempDirAudio,
         videodir=args.tempDirVideo,
         dest =args.destination
    )
