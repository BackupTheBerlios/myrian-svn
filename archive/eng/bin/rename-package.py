#!/usr/bin/python

# Author:  Vadim Nasardinov (vadimn@redhat.com)
# Since:   2004-09-28
# Version: $Id: //eng/persistence/dev/bin/rename-package.py#1 $

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
        import getopt
        self._options = {}
        flags = ["from=", "to=", "help"]
        (options, self.directories) = getopt.getopt(opts, "", flags)
        if not options:
            display_help_and_exit()

        for (key, value) in options:
            # a key start with "--" and possibly ends in "="
            self._options[key[2:].rstrip("=")] = value

        if len(self.directories) == 0:
            print "Error: No directories are given.\n"
            display_help_and_exit()

        for dd in self.directories:
            if not isdir(dd):
                print "Error:", dd, "is not a directory.\n"
                display_help_and_exit()

    def get_from(self):
        return self._options["from"]

    def get_to(self):
        return self._options["to"]

    def help_requested(self):
        return self._options.has_key("help")

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
    def __init__(self, ext):
        self.has_correct_extension = ext_filter(ext)
        self._count = 0

    def __call__(self, fname):
        if not self.has_correct_extension(fname): return
        print fname
        self._count += 1

    def get_count(self):
        return self._count

def rename_package(config):
    walker = Walker()
    extensions = [".java", ".jj", ".pdl"]
    for ext in extensions:
        for dir in config.get_directories():
            print "# Walking", dir, "looking for", ext, "files"
            processor = Processor(ext)
            walk(dir, walker, processor)
            print "# Processed %d %s files" % (processor.get_count(), ext)

if __name__ == '__main__':
    config = Config(sys.argv[0], sys.argv[1:])
    if config.help_requested():
        display_help_and_exit()
    rename_package(config)
