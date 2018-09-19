## What this is
There are many sophisticated algorithms for calculating edit distance or the number of edits needed to change one string into another string. These algorithms compute a variety of metrics that count different types of edits (additions, deletions, substitutions, and/or transpositions). 

Quill packages three of these algorithms into a simple API.

## How to use it
Once a Quill instance has been created, you can modify the costs (or weights) assigned to different types of edits (as well as building up a map of pairs of characters to a cost for substitution). You can then call the `computeCosts()` method as follows.
```
Quill quill = new Quill();
quill.setAdditionCost(1.5);
quill.setDeletionCost(1.0);
quill.addSubstitutionRule('f', 'g', 1.5);

System.out.println(quill.computeCost("iffru", "figure", OperationType.DEFAULT));
```
