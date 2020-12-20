Да се напише select јамка која ќе ја излиста секоја датотека од тековниот
именик и ќе му овозможи на корисникот да ја погледне содржината на
датотеката со одбирање на нејзиниот број.

________________________________________________________________________________


#!/bin/bash

allFiles=`ls`
select file in $allFiles "Exit"
do
    if [ "$file" = "Exit" ]
    then
        break
    elif [ -d "$file" ]
    then
        echo "$file is a folder"
        continue
    fi
    echo $file
    cat $file
done