#!/usr/bin/python

# Author:  Vadim Nasardinov (vadimn@redhat.com)
# Since:   2004-09-28
# Version: $Id: //eng/persistence/dev/bin/rename-package.py#2 $

'''Usage:
    $ rename-package.py --from <old-package-name> --to <new-package-name> <dir1> [<dir2> ...]
Example:
    $ rename-package.py --from com.arsdigita --to cap src test/src
'''

import sys
from os.path import join, isdir, isfile, walk

class Config:
    '''Represents command-line options'''

    def __init__(self, script, opts):
        self._script = script

        import getopt
        hash = {}
        flags = ["from=", "to=", "help"]
        (options, self.directories) = getopt.getopt(opts, "", flags)
        if not options:
            display_help_and_exit()

        for (key, value) in options:
            # an option starts with "--" and possibly ends in "="
            hash[key[2:].rstrip("=")] = value

        if len(self.directories) == 0:
            print "Error: No directories are given.\n"
            display_help_and_exit()

        for dd in self.directories:
            if not isdir(dd):
                print "Error:", dd, "is not a directory.\n"
                display_help_and_exit()
        self._from_pkg = hash["from"]
        self._to_pkg   = hash["to"]
        self._help = hash.has_key("help")

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

    def help_requested(self):
        return self._help

    def get_directories(self):
        return list(self.directories)


def display_help_and_exit():
    print __doc__
    sys.exit(0)

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

def rename_package(config):
    walker = Walker()
    extensions = [".java", ".jj", ".pdl"]
    for ext in extensions:
        for dir in config.get_directories():
            print "\n# Walking", dir, "looking for", ext, "files"
            processor = Processor(ext, config)
            walk(dir, walker, processor)
            print "#", processor

if __name__ == '__main__':
    config = Config(sys.argv[0], sys.argv[1:])
    if config.help_requested():
        display_help_and_exit()
    rename_package(config)
