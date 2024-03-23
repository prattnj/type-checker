#!/bin/bash
while true; do
    read -p "" line

    # Check if the user wants to finish entering expressions
    if [ "$line" == "" ]; then
        break
    fi

    # Append the expression to the existing expressions
    expressions="${expressions}${line}\n"
done

echo -e "$expressions" | java -jar type-checker.jar
