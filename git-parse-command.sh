#!/bin/bash
#
# Ouput:
#  SHA1
#  Unix Timestamp
#  Author-Name
#  Subject
#  <deleted lines> <added lines> <path>
#
git log master -w -b -R --numstat --no-merges --date-order --format="format:%H%n%at%n%aN%n%s" --no-renames
