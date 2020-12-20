Да се напише скрипта која ќе прима неограничен број на имиња на датотеки
како аргументи и на секој фајл ќе му додаде наставка “.txt”, а за секој фолдер ќе
го испечати бројот на датотеки во него.

________________________________________________________________________________


#!/bin/bash

for file in $*
do
    if [ -f "$file ]
    then
        echo "Go preimenuvam $file vo ${file}.txt"
        mv $file ${file}.txt
    elif [ -d "$file" ]
    then
        number=`ls $file | wc -w`
        echo "Folderot $file ima $number fajlovi vo nego"
    fi
done