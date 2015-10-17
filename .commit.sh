#!/bin/bash

for i in `seq 1 10`;
do
    echo "hi" >> .hidden
    git add .
    git commit -m "updating script"
    git pull --rebase
    git push origin master
done
