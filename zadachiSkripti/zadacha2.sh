Да се напише скрипта која како аргументи ќе прима имиња на два фолдери и ќе
провери дали во двата постои датотека која ја има истата содржина. Доколку
постои на излез да се испечати “Found a match” и содржината на датотеката.

________________________________________________________________________________

#!/bin/bash

if [ $# -lt 2 ]
then
    echo "Not enough args"
    exit 1
fi

if [ ! -d $1 ] || [ ! -d $2 ]
then
    echo "$1 or $2 is not a directory"
    exit 1
fi


files1=`ls $1`
files2=`ls $2`
for file1 in $files1
do
    content1=`cat $1/$file1`
    for file2 in $files2
    do
        content2=`cat $2/$file2`
        if [ "$content1" = "$content2" ]
        then
            echo "Found a match"
            echo "$content1"
    done
done