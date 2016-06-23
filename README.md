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
A number of transformations 
on both input and result matrices
can be helpful in interpreting and analysing the results.

## Usage

    java -jar exit.jar inputfile.csv [options...]
    
Inputfile name is a mandatory argument.

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

## EXIT method



## Interpretation of results






