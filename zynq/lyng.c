#include <stdio.h>
#include <stdint.h>
#include <stdlib.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/mman.h>
#include <sys/ioctl.h>

#define CMA_ALLOC _IOWR('Z', 0, uint32_t)

int main()
{
  int fd;
  volatile uint8_t *rst;
  volatile void *cfg;
  volatile int16_t *ram;

  uint32_t size;
  int16_t value;

  FILE *fp;
  int i, address;
  uint16_t instr;
  int c[2];

  if((fd = open("/dev/mem", O_RDWR)) < 0)
  {
    perror("mem");
    return EXIT_FAILURE;
  }

  cfg = mmap(NULL, sysconf(_SC_PAGESIZE), PROT_READ|PROT_WRITE, MAP_SHARED, fd, 0x40000000);

  close(fd);

  if((fd = open("/dev/cma", O_RDWR)) < 0)
  {
    perror("cma");
    return EXIT_FAILURE;
  }

  size = 1024*sysconf(_SC_PAGESIZE);

  if(ioctl(fd, CMA_ALLOC, &size) < 0)
  {
    perror("ioctl");
    return EXIT_FAILURE;
  }

  ram = mmap(NULL, 1024*sysconf(_SC_PAGESIZE), PROT_READ|PROT_WRITE, MAP_SHARED, fd, 0);

  rst = (uint8_t *)(cfg + 0);

  // set writer address
  *(uint32_t *)(cfg + 8) = size;

  // set number of samples
  *(uint16_t *)(cfg + 6) = 65536 - 1;

  // reset writer and packetizer
  *rst &= ~1;
  *rst |= 1;

  // wait 1 second
  sleep(1);

  // set load mode
  *rst |= 2;

  fp = fopen("program.bin", "r");
  if(fp == NULL) {
    perror("file");
    exit(1);
  }

  address = 0;
  c[0] = getc(fp);
  c[1] = getc(fp);

  while(c[0] != EOF) {
    *(uint16_t *)(cfg + 2) = address;
    *(uint16_t *)(cfg + 4) = (c[0] << 8 | c[1]);
    address++;
    c[0] = getc(fp);
    c[1] = getc(fp);
  }

  // Set run mode
  *rst &= ~2;

  sleep(1);

  // print IN1 and IN2 samples
  for(i = 0; i < 24 - 1; ++i)
  {
    value = ram[i];
    printf("%d, ", ram[i]);
  }
  printf("\n");

  return EXIT_SUCCESS;
}
