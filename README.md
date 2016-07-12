# EXIT -- Express Cross-Impact Technique

**EXIT** is a Java program that performs 
an EXIT (Express Cross-Impact Technique) style
cross-impact analysis 
on an input file containing a cross-impact matrix 
which describes the direct impacts between variables.

The main output of the EXIT calculation
is a new cross-impact matrix which describes 
the summed direct and indirect impacts
that are mined in the EXIT calculation process
on the basis of the input matrix.
Certain transformations 
on both input and result matrices
can be helpful in interpreting and analysing the results.

## Usage

    java -jar exit.jar inputfile.csv [options...]

Inputfile name is a mandatory argument.
    
Example:

    java -jar exit.jar directimpactmatrix.csv -max 5 -t 0.005 -extra
    
Reads the input matrix from file `directimpactmatrix.csv`. 
Sets the maximum impact value to 5.
Sets the treshold value to 0.005.
Asks to print out the extra reports about the cross-impact calculation.


## Options

`-o` Output file name
  
`-max` Maximum value allowed in the impact matrix. 
This value is also used in the EXIT calculation. 
See EXIT method section for details.

`-t` Threshold value used in mining the indirect impacts from the impact chains.
The lower the threshold value, the longer the calculation will take 
and the more indirect impacts through the impact network will be discovered.
Default threshold is 0.1.

`-sep` Separator character used in input data. Default is ';'.

`-of` Print impact chains starting with variable with this index

`-on` Print impact chains ending in variable with this index

NOTE: If neither `-of` or `-on` options are present, a cross-impact matrix describing 
all summed direct and indirect impacts between variables is calculated.

`-int` If this flag is present, input matrix values are assumed to be integers

`-extra` If this flag is present, extra reports are printed

## Input file

The input file should contain an impact matrix that describes 
the direct impacts between the variables included in the cross-impact analysis.
The file should have as many rows as there are variables.
Each row should have the variable name 
followed by the impacts of that variable on all other variables, 
all data separated by the separator character.
This means that each row should, in addition to the variable name, 
have as many impacts as there are variables (or rows) in the input file.
Empty rows in the file will be ignored.

### Example of a valid input file

        V1;0;0;-1;-4;-3;5;-2;1;0;-3;0;-1
        V2;1;0;-1;3;-4;-3;-1;-4;-3;-2;5;-3
        V3;-4;-1;0;-1;-5;-4;1;1;-2;-1;1;-2
        V4;-4;4;0;0;2;-5;-2;3;1;-3;-5;3
        V5;3;5;-1;4;0;-2;4;-3;5;5;-3;3
        V6;-3;2;0;-2;-3;0;2;5;-4;-3;1;5
        V7;5;3;2;1;-2;-4;0;0;-3;-4;1;0
        V8;-1;-5;0;5;4;0;3;0;-5;5;5;-3
        V9;-2;4;-1;3;-1;4;-1;-1;0;-3;1;-4
        V10;0;1;0;-3;2;-5;0;-3;-3;0;-4;-3
        V11;3;5;3;-5;5;-1;1;2;3;-1;0;-5
        V12;4;-3;-5;0;3;5;-1;5;-4;5;4;0

## EXIT method



## Interpretation of results






