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

`-t` Treshold value used in mining the indirect impacts from the impact chains.
The lower the treshold value, the longer the calculation will take 
and the more indirect impacts through the impact network will be discovered.

`-sep` Separator character used in input data

`-of` Print impact chains starting with variable with this index

`-on` Print impact chains ending in variable with this index

`-int` If this flag is present, input matrix values are assumed to be integers

`-extra` If this flag is present, extra reports are printed

## Input file

## EXIT method


## Interpretation of results






