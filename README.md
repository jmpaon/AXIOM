# AXIOM

This java program implements the AXIOM method.
AXIOM is a cross-impact modeling and analysis technique.
Detailed description of the approach can be found [here](http://urn.fi/URN:NBN:fi:uta-201611142562).


## Usage 
```
java -jar AXIOM.jar inputfile evaluationcount 
```
for example 
```
java -jar AXIOM.jar my_axiom_data.txt 50000 
```
will read an AXIOM model from the file "my_axiom_data.txt" and perform 50000 evaluations per iteration.


## Input file syntax

### Statement addition

Statement is added to AXIOM model with the following syntax:

```
# statement_label [ts n] [INT] [statement description]
```
the following line 

```
# economy ts 2 INT Economic growth
```
will add a statement with label "economy", with a timestep of 2, as an intervention statement to the AXIOM model.

### Option addition

Option is added to AXIOM model with the following syntax:

```
* option_label a_priori_probability
```

the following line 

```
* low_growth 0.3
```
will add an option with label "low growth" and an a priori probability of 0.3 to the AXIOM model.
Option is added under the statement last introduced.

### Impact addition

Impact is added to AXIOM model with the following syntax:

```
> target_statement_label target_option_label adjustment_function_name
```

the following line 

```
> pollution high +2
```
will add an impact to option with label "high" under statement "pollution" using probability adjustment function "+2".
Impact is added under the option last introduced.

