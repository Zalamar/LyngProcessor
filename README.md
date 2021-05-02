# Lyng Processor
Lyng is 16-bit pipelined processor created as a part of the Advanced Computer Architecture course at Technical University of Denmark.

## Get Started

### Run simulation
1. Install Chisel ([instructions](https://www.imm.dtu.dk/~masca/chisel-book.pdf))
2. Initialise assembler submodule.

```
$ git submodule init
$ git submodule update
```

3. Write your assembly code (reference for Lyng ISA in assembler README) or take one of the provided examples and place it in `program.lisa` file at top level of the repository.

4. Compile and run in Scala simulator.

```
TODO: fill in command
```

5. Anything written to `0xFFFE` memory location will be printed into the terminal by simulator.

### Synthetise Verilog

```
sbt run
```

## Project Team

* [Lukas Kyzlik](https://github.com/garnagar)
* [Dario Pasarello](https://github.com/dario-passarello)
* [Tobias Rydberg](https://github.com/Zalamar)
