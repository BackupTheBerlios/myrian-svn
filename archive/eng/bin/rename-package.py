#!/usr/bin/python

# Author:  Vadim Nasardinov (vadimn@redhat.com)
# Since:   2004-09-28
# Version: $Id: //eng/persistence/dev/bin/rename-package.py#4 $

'''Usage:
    $ rename-package.py --from <old-package-name> --to <new-package-name> <dir1> [<dir2> ...]
Example:
    $ rename-package.py --from com.arsdigita --to cap src cap test bin conf doc sql
'''

import sys
import os
from os.path import join, isdir, isfile, splitext, walk

class Config:
    '''Represents command-line options'''

    def __init__(self):
        from optparse import OptionParser, OptionGroup
        self._script = sys.argv[0]
        usage = "\n\t%prog [options] dir1 [dir2 ...]"
        parser = OptionParser(usage)
        self._parser = parser
        u_opts = OptionGroup(parser, "User options",
                             "User-facing options.")

        u_opts.add_option("-f", "--from", dest="from_pkg",
                          metavar="FROM_PKG",
                          help="[required] (Part of) a java package name that" +
                          " you'd like to rename, e.g. 'com.arsdigita'")
        u_opts.add_option("-t", "--to", dest="to_pkg", metavar="TO_PKG",
                          help="[required] The new (possibly partial) java" +
                          "package name to which FROM_PKG is to be renamed," +
                          " e.g. 'org.acme'.")
        parser.add_option_group(u_opts)
        special = OptionGroup(parser, "Special options",
                              "Only used by auto-generated shell scripts." +
                              " Do not use, unless you know what you're doing.")
        special.add_option("-r", "--replace", action="store_true",
                           dest="replace",
                           help="Actually edit the supplied file in place.")
        parser.add_option_group(special)

        (options, self._paths) = parser.parse_args()

        if len(self._paths) == 0:
            self.error("Error: No paths given.")

        self._from_pkg = options.from_pkg
        self._to_pkg   = options.to_pkg
        self._replace_option = options.replace

        if options.replace:
            if not isfile(self.get_fname()):
                self.error("Error: %s is not a file." % self.get_fname())
        else:
            for dd in self._paths:
                if not isdir(dd):
                    self.error("Error:", dd, "is not a directory.")

    def get_script(self):
        return self._script

    def get_from_pkg(self):
        return self._from_pkg

    def get_from_dir(self):
        return self._from_pkg.replace(".", "/")

    def get_to_pkg(self):
        return self._to_pkg

    def get_to_dir(self):
        return self._to_pkg.replace(".", "/")

    def replace_requested(self):
        return self._replace_option

    def get_fname(self):
        if self.replace_requested():
            return self._paths[0]
        else:
            return None

    def get_directories(self):
        if self.replace_requested():
            return None
        else:
            return list(self._paths)

    def error(self, msg):
        self._parser.error("%s\nTry\n\t%s --help" % (msg, self._script))

def ext_filter(extension):
    return lambda fname: fname.endswith(extension)


class Walker:
    def __init__(self):
        pass

    def __call__(self, process, dirname, fnames):
        basenames = [join(dirname, ff) for ff in fnames \
                     if isfile(join(dirname, ff))]

        for ff in basenames:
            process(ff)

class Processor:
    def __init__(self, ext, config):
        self._config = config
        self._ext = ext
        self.has_correct_extension = ext_filter(ext)
        self._count = 0
        self._moved = 0

    def __call__(self, fname):
        if not self.has_correct_extension(fname): return
        to_name = self._move(fname)
        if not to_name is None:
            self._replace(fname, to_name)
        else:
            self._replace(fname, fname)
        self._count += 1

    def _move(self, from_name):
        from_dir = "/" + self._config.get_from_dir() + "/"
        if from_name.find(from_dir) < 0: return None
        to_dir = "/" + self._config.get_to_dir() + "/"

        (before, after) = from_name.split(from_dir)
        to_name = before + to_dir + after
        print "p4 integrate %s %s" % (from_name, to_name)
        print "p4 delete %s" % from_name
        self._moved += 1
        return to_name

    def _replace(self, from_name, to_name):
        (root, ext) = splitext(from_name)
        if ext in ("jpg",): return

        ff = open(from_name)
        needs_editing = False
        pkg = self._config.get_from_pkg()
        dir = self._config.get_from_dir()
        for line in ff:
            for pattern in (pkg, dir):
                if line.find(pattern) > -1:
                    needs_editing = True
                    break
        ff.close()
        if needs_editing:
            cc = self._config
            print "p4 edit", to_name
            print "%s --replace --from %s --to %s %s" % \
                  (cc.get_script(), cc.get_from_pkg(), cc.get_to_pkg(), to_name)

    def __str__(self):
        return "Moved: %d out of %d %s files" % \
               (self._moved, self._count, self._ext )

def generate_script(config):
    walker = Walker()

    extensions = ("java", "properties", "jj", "pdl", "xml", "sql", \
                  "txt", "xsl", "csv", "dat", "dtd", "html", "jdo", "jpg")
    extensions = ["." + ext for ext in extensions]

    print "#!/bin/bash\n"

    for ext in extensions:
        for dir in config.get_directories():
            print "\n# Walking", dir, "looking for", ext, "files"
            processor = Processor(ext, config)
            walk(dir, walker, processor)
            print "#", processor

def edit_in_place(config):
    fname = config.get_fname()
    if not isfile(fname):
        config.error("Error: %s is not a file." % fname)

    if not os.access(fname, os.W_OK):
        config.error("Error: %s is not writable." % fname)

    from_pkg = config.get_from_pkg()
    to_pkg   = config.get_to_pkg()
    from_dir = config.get_from_dir()
    to_dir   = config.get_to_dir()

    for line in fileinput.input(files=fname, inplace=1):
        line = line.rstrip("\n\r ")
        line = line.replace(from_pkg, to_pkg)
        line = line.replace(from_dir, to_dir)
        print line

if __name__ == '__main__':
    config = Config()
    if config.replace_requested():
        import fileinput
        edit_in_place(config)
    else:
        generate_script(config)
