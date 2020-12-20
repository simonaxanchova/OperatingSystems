Да се напише скрипта која ќе ги организира датотеките од тековниот именик
според екстензијата. Организацијата се врши преку копирање на датотеките во
соодветни именици, чии имиња се еднакви на екстензиите. Имениците треба
да се креираат во именик чија локација е зададена како прв аргумент на
скриптата. Доколку овој именик не постои да се креира. Доколку постои, да се
избришат сите датотеки и именици кои се наоѓаат во него. 

________________________________________________________________________________

#!/bin/bash

if [ $# -lt 1 ]
then
    echo "USAGE: $0 tuka"
    exit 1
fi

if [ -d $1 ]
then
    rm -r $1
fi
mkdir $1

files=`ls *.*`
for file in $files
do
    ext=`echo $file | sed 's/.*\.//'`
    if [ ! -d $1/$ext ]
    then
        mkdir $1/$ext
    fi
    echo "Copying $file to $1/$ext"
    cp $file $1/$ext
done