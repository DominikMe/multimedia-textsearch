#!/usr/bin/python
#-*- encoding: utf-8 -*-

import logging
import os
import tempfile
import errno
import argparse
from string import Template
from multiprocessing import Pool
import lxml.etree as ET

from path import path


#simple logging
FORMAT = '%(levelname)s\t: %(message)s'
logging.basicConfig(format=FORMAT, level=logging.DEBUG)
clilogger = logging.getLogger('cli')
clilogger.debug("cli logger is init")

COMMAND_EXTRACT_AUDIO = Template(
    "ffmpeg -i ${src} -vn -ar 44100 -ac 2 -ab 192 --ss ${start} -t ${duration} -f flac ${out}.flac")
COMMAND_EXTRACT_VIDEO = Template("ffmpeg -y -i ${src} -r ${framerate}  -f image2 ${dir}/%07d.png")
COMMAND_TESSERACT = Template("tesseract ${input} ${out} -l ${lang} -psm ${psm}")
COMMAND_EXTRACT_DURATION = Template("ffmpeg -i ${src}")


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
    cout,cin,cerr= os.popen3(s)

    import re
    pattern = re.compile(r'Duration: (\d\d:\d\d:\d\d.\d\d)')


    for line in cerr.readlines():
        line = line.strip()
        m = pattern.findall(line)
        print line, m
        if m:
            t = m[0]
            tim = int(t[0:2]) * 60 * 60 + int(t[3:5]) * 60 + int(t[6:8])
            clilogger.info("in seconds: %d", tim)
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
        p = {'src': input_video, 'out': t / deltaT, 'start': t, 'duration': deltaT}
        s = COMMAND_EXTRACT_AUDIO.substitute(p)
        clilogger.info("exec: %s in worker", s)
        pool.apply_async(os.system, s)

    pool.close()


def extract_video(input_video, target_folder, deltaT=5):
    cmd = COMMAND_EXTRACT_VIDEO.substitute(
        {'src': input_video, 'dir': target_folder, 'framerate': 1. / deltaT})
    clilogger.info(cmd)
    mkdir_p(target_folder)
    os.system(cmd)


def gather_files(target_folder, filter="*.png", deltaT=5):
    def time(fil):
        name, ext = os.path.splitext(fil)
        pos = int(os.path.basename(name))
        return (pos - 1) * deltaT

    files = [(time(fil), fil) for fil in path(target_folder).listdir(filter)]
    files = sorted(files, key=lambda a: a[0])
    return files


def ocr_image(image, lang="deu", psm=1):
    out = tempfile.mktemp()

    cmd = COMMAND_TESSERACT.substitute(
        {'input': image, 'out': out, 'lang': lang, 'psm': psm})
    clilogger.info(cmd)
    a = os.system(cmd)
    if a == 0:
        with open("%s.txt" % out) as f:
            return f.read()
    raise Exception("error in tesseracting %s" % image)


def ocr(doc):
    time, image = doc[0], doc[1]
    ndoc = ocr_image(image)
    return (time, ndoc)


def sr(doc):
    time, audio = doc[0], doc[1]
    #ndoc = speechrec(image)
    return (time, "")


def print_timed_document(input_file, docs):
    root = ET.Element("timed-document", file = input_file)
    for time,content in docs:
        slot = ET.SubElement(root, 'slot', start = time, end = time + deltaT)
        slot.text = content
    print ET.tostring(root)

def main(inputfile, video=True, audio=True,
         deltaT=60, audiodir=None, videodir=None):
    newAudioDir = audiodir if audiodir else tempfile.mkdtemp()
    newPictureDir = videodir if videodir else tempfile.mkdtemp()

    clilogger.info("use %s for pictures", newPictureDir)
    clilogger.info("use %s for pictures", newAudioDir)

    pool = Pool(4)

    if audio:
        extract_audio(inputfile, newAudioDir, deltaT)
        audios = gather_files(newAudioDir,  "*flac", deltaT)
        adocs = pool.map(sr, audios)

    if video:
        extract_video(inputfile, newPictureDir, deltaT)
        images = gather_files(newPictureDir, "*.png", deltaT)
        vdocs = pool.map(ocr, images)

    pool.close()

    print type(adocs), type(vdocs)
    print list(adocs), list(vdocs)

    docs = []
    for (at,ac), (vt,vc) in zip(adocs,vdocs):
        docs.append( (at,ac+vc)  )
    print_timed_document(inputfile, docs)


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
    return parser


if __name__ == "__main__":
    p = get_parser()
    args,input = p.parse_known_args()

    main(inputfile=args.input,
         video=not args.novideo,
         audio=not args.noaudio,
         deltaT=int(args.deltaT),
         audiodir=args.tempDirAudio,
         videodir=args.tempDirVideo,
    )
