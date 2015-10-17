#!/bin/bash
git add .
git commit -m "updating script"
git pull --rebase
git push origin master
