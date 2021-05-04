# Lyng Processor
Lyng is 16-bit pipelined processor created as a part of the Advanced Computer Architecture course at Technical University of Denmark.

## Get Started

### Run simulation
1. Install Chisel ([instructions](https://www.imm.dtu.dk/~masca/chisel-book.pdf))

2. Write your assembly code (reference for Lyng ISA in assembler README) or use one of the provided examples.

3. Compile and run in Scala simulator.

```
$ make PROGRAM=<path> simulate
```

If the path is not provided, simulation will attempt to use `program.lisa` in top level of the repository.

4. Anything written to `0xFFFE` memory location will be printed into the terminal by simulator.

### Get waveforms
```
$ make PROGRAM=<path> vcd
```

### Synthetise Verilog

```
$ sbt run
```

## Project Team

* [Lukas Kyzlik](https://github.com/garnagar)
* [Dario Pasarello](https://github.com/dario-passarello)
* [Tobias Rydberg](https://github.com/Zalamar)
