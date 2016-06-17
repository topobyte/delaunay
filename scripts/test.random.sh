#!/bin/bash

DIR=$(dirname $0)
CMD="$DIR/delaunay.sh"
CLASS="de.topobyte.paulchew.delaunay.TestRandom"

exec "$CMD" "$CLASS" "$@"
