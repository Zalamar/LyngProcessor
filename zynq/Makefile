cc=gcc
C = lyng.c
LISA = program.lisa

default:
	cc $(C) -o lyng.o
	python3 lyng-assembler/lyng-assembler.py $(LISA) program.bin
	cat system_wrapper.bit > /dev/xdevcfg
	./lyng.o
