/*
 * Tester for the ALU
 *
 * Author: Tobias Rydberg (tobiasrydberg@pm.me)
 *
 */

package lyng.execute

import chisel3._
import chisel3.iotesters.PeekPokeTester
import org.scalatest.{FlatSpec, Matchers}

import scala.util.Random

class EXTester(dut: ExecuteTop) extends PeekPokeTester(dut) {

  val testSize = 20
  val r = new Random()
  val start = -32768
  val end = 32767

  var a = 0
  var b = 0
  var c = 0
  var result = 0
  var debug = false

  poke(dut.io.in.imm, 0)
  poke(dut.io.in.ex_forward, 0)
  poke(dut.io.in.me_forward, 0)
  poke(dut.io.in.prop_rs1, 0)
  poke(dut.io.in.prop_rs2, 0)
  poke(dut.io.ctrl.alu_src, 0)
  poke(dut.io.ctrl.ext_mode, 0)
  poke(dut.io.ctrl.jmp_mode, 0)

  // ADD
  if (debug) {print("Testing ADD")}
  for (i <- 1 to testSize) {
    a = start + r.nextInt((end - start) + 1)
    b = start + r.nextInt((end - start) + 1)

    poke(dut.io.in.rs1, a)
    poke(dut.io.in.rs2, b)
    poke(dut.io.ctrl.alu_op, 2)
    result = a+b
    if (result < 0) {result = result + 65536}
    expect(dut.io.out.alu_res,  result)
  }

  // ADC
  if (debug) {print("Testing ADC")}
  result = 21845 + 21845
  poke(dut.io.in.rs1, 21845)
  poke(dut.io.in.rs2, 21845)
  poke(dut.io.ctrl.alu_op, 2)
  if (result < 0) {result = result + 65536}
  step(1)
  expect(dut.io.out.alu_res, result)

  result = 1021
  poke(dut.io.in.rs1, 382)
  poke(dut.io.in.rs2, 638)
  poke(dut.io.ctrl.alu_op, 3)
  expect(dut.io.out.alu_res, result)
  step(1)

  // SUB
  if (debug) {print("Testing SUB")}
  for (i <- 1 to testSize) {
    a = start + r.nextInt((end - start) + 1)
    b = start + r.nextInt((end - start) + 1)

    poke(dut.io.in.rs1, a)
    poke(dut.io.in.rs2, b)
    poke(dut.io.ctrl.alu_op, 4)
    result = a-b
    if (result < 0) {result = result + 65536}
    expect(dut.io.out.alu_res, result)
  }

  // SBB
  if (debug) {print("Testing SBB")}
  result = -21845 - 21845
  poke(dut.io.in.rs1, -21845)
  poke(dut.io.in.rs2, 21845)
  poke(dut.io.ctrl.alu_op, 4)
  if (result < 0) {result = result + 65536}
  expect(dut.io.out.alu_res, result)
  step(1)

  result = 255
  poke(dut.io.in.rs1, 638.S)
  poke(dut.io.in.rs2, 382.S)
  poke(dut.io.ctrl.alu_op, 5)
  expect(dut.io.out.alu_res, result)
  step(1)

  // AND
  if (debug) {print("Testing AND")}
  for (i <- 1 to testSize) {
    a = start + r.nextInt((end - start) + 1)
    b = start + r.nextInt((end - start) + 1)

    poke(dut.io.in.rs1, a)
    poke(dut.io.in.rs2, b)
    poke(dut.io.ctrl.alu_op, 8)
    result = a&b
    if (result < 0) {result = result + 65536}
    expect(dut.io.out.alu_res, result)
  }

  // OR
  if (debug) {print("Testing OR")}
  for (i <- 1 to testSize) {
    a = start + r.nextInt((end - start) + 1)
    b = start + r.nextInt((end - start) + 1)

    poke(dut.io.in.rs1, a)
    poke(dut.io.in.rs2, b)
    poke(dut.io.ctrl.alu_op, 9)
    result = a|b
    if (result < 0) {result = result + 65536}
    expect(dut.io.out.alu_res, result)
  }

  // XOR
  if (debug) {print("Testing XOR")}
  for (i <- 1 to testSize) {
    a = start + r.nextInt((end - start) + 1)
    b = start + r.nextInt((end - start) + 1)

    poke(dut.io.in.rs1, a)
    poke(dut.io.in.rs2, b)
    poke(dut.io.ctrl.alu_op, 10)
    result = a^b
    if (result < 0) {result = result + 65536}
    expect(dut.io.out.alu_res, result)
  }

  // NOT
  if (debug) {print("Testing NOT")}
  for (i <- 1 to testSize) {
    a = start + r.nextInt((end - start) + 1)

    poke(dut.io.in.rs1, a)
    poke(dut.io.in.rs2, 0)
    poke(dut.io.ctrl.alu_op, 11)
    result = ~a
    if (result < 0) {result = result + 65536}
    expect(dut.io.out.alu_res, result)
  }

  // SHIFTL
  if (debug) {print("Testing SHIFTL")}
  for (i <- 1 to testSize) {
    a = start + r.nextInt((end - start) + 1)
    b = r.nextInt(15)

    poke(dut.io.in.rs1, a)
    poke(dut.io.in.rs2, b)
    poke(dut.io.ctrl.alu_op, 12)
    result = a << b
    while (result >= 65536) {result = result - 65536}
    while (result < 0) {result = result + 65536}
    expect(dut.io.out.alu_res, result)
  }

  // SHIFTR
  if (debug) {print("Testing SHIFTR")}
  for (i <- 1 to testSize) {
    a = start + r.nextInt((end - start) + 1)
    b = r.nextInt(15)

    poke(dut.io.in.rs1, a)
    poke(dut.io.in.rs2, b)
    poke(dut.io.ctrl.alu_op, 13)
    result = a.toChar >> b
    while (result > 65536) {result = result - 65536}
    while (result < 0) {result = result + 65536}
    expect(dut.io.out.alu_res, result)
  }

  // ADDI
  if (debug) {print("Testing ADDI")}
  poke(dut.io.ctrl.alu_src, 1)
  for (i <- 1 to testSize) {
    a = start + r.nextInt((end - start) + 1)
    b = r.nextInt(32)

    poke(dut.io.in.rs1, a)
    poke(dut.io.in.imm, b)
    poke(dut.io.ctrl.alu_op, 2)
    result = a+b
    if (result < 0) {result = result + 65536}
    expect(dut.io.out.alu_res, result)
  }
  poke(dut.io.ctrl.alu_src, 0)

  // SUBI
  if (debug) {print("Testing SUBI")}
  poke(dut.io.ctrl.alu_src, 1)
  for (i <- 1 to testSize) {
    a = start + r.nextInt((end - start) + 1)
    b = r.nextInt(32)

    poke(dut.io.in.rs1, a)
    poke(dut.io.in.imm, b)
    poke(dut.io.ctrl.alu_op, 4)
    result = a-b
    if (result < 0) {result = result + 65536}
    expect(dut.io.out.alu_res, result)
  }
  poke(dut.io.ctrl.alu_src, 0)

  // MVIH
  if (debug) {print("Testing MVIH")}
  poke(dut.io.ctrl.alu_src, 1)
  poke(dut.io.ctrl.ext_mode, 2)
  for (i <- 1 to testSize) {
    a = start + r.nextInt((end - start) + 1)
    b = start + r.nextInt((end - start) + 1)

    poke(dut.io.in.imm, a)
    a = ((a & 0x0700) >> 3) | (a & 0x001F)
    poke(dut.io.in.rs1, b)
    poke(dut.io.ctrl.alu_op, 14)
    result = (a << 8) | (b & 0x00FF)
    if (result < 0) {result = result + 65536}
    expect(dut.io.out.alu_res, result)
  }
  poke(dut.io.ctrl.alu_src, 0)
  poke(dut.io.ctrl.ext_mode, 0)

  // MVIL
  if (debug) {print("Testing MVIL")}
  poke(dut.io.ctrl.alu_src, 1)
  poke(dut.io.ctrl.ext_mode, 2)
  for (i <- 1 to testSize) {
    a = start + r.nextInt((end - start) + 1)
    b = start + r.nextInt((end - start) + 1)

    poke(dut.io.in.imm, a)
    a = ((a & 0x0700) >> 3) | (a & 0x001F)
    poke(dut.io.in.rs1, b)
    poke(dut.io.ctrl.alu_op, 15)
    result = (b & 0xFF00) | (a & 0x00FF)
    expect(dut.io.out.alu_res, result)
  }
  poke(dut.io.ctrl.alu_src, 0)
  poke(dut.io.ctrl.ext_mode, 0)

  // LDIDR
  if (debug) {print("Testing LDIDR")}
  poke(dut.io.ctrl.alu_src, 1)
  poke(dut.io.ctrl.ext_mode, 1)
  for (i <- 1 to testSize) {
    a = start + r.nextInt((end - start) + 1)
    b = -16 + r.nextInt((15 - -16) + 1)

    poke(dut.io.in.rs1, a)
    poke(dut.io.in.imm, b)
    poke(dut.io.ctrl.alu_op, 2)
    result = a+b
    if (result < 0) {result = result + 65536}
    expect(dut.io.out.alu_res, result)
  }
  poke(dut.io.ctrl.alu_src, 0)
  poke(dut.io.ctrl.ext_mode, 0)

  // STIDR
  if (debug) {print("Testing STIDR")}
  poke(dut.io.ctrl.alu_src, 1)
  poke(dut.io.ctrl.ext_mode, 1)
  for (i <- 1 to testSize) {
    a = start + r.nextInt((end - start) + 1)
    b = -16 + r.nextInt((15 - -16) + 1)

    poke(dut.io.in.rs1, a)
    poke(dut.io.in.imm, b)
    poke(dut.io.ctrl.alu_op, 2)
    result = a+b
    if (result < 0) {result = result + 65536}
    expect(dut.io.out.alu_res, result)
  }
  poke(dut.io.ctrl.alu_src, 0)
  poke(dut.io.ctrl.ext_mode, 0)

  // LDIDX
  if (debug) {print("Testing LDIDX")}
  for (i <- 1 to testSize) {
    a = start + r.nextInt((end - start) + 1)
    b = start + r.nextInt((end - start) + 1)

    poke(dut.io.in.rs1, a)
    poke(dut.io.in.rs2, b)
    poke(dut.io.ctrl.alu_op, 2)
    result = a+b
    if (result < 0) {result = result + 65536}
    expect(dut.io.out.alu_res, result)
  }

  // STIDX
  if (debug) {print("Testing STIDX")}
  for (i <- 1 to testSize) {
    a = start + r.nextInt((end - start) + 1)
    b = start + r.nextInt((end - start) + 1)

    poke(dut.io.in.rs1, a)
    poke(dut.io.in.rs2, b)
    poke(dut.io.ctrl.alu_op, 2)
    result = a+b
    if (result < 0) {result = result + 65536}
    expect(dut.io.out.alu_res, result)
  }

  // JMPI
  poke(dut.io.ctrl.ext_mode, 5)
  poke(dut.io.ctrl.jmp_mode, 7)
  if (debug) {print("Testing JMPI")}
  for (i <- 1 to testSize) {
    a = -1024 + r.nextInt((1023 - -1024) + 1)

    poke(dut.io.in.imm, a)
    result = a
    if (result < 0) {result = result + 65536}
    expect(dut.io.out.jump_amt, result)
    expect(dut.io.out.jump, 1)
  }
  poke(dut.io.ctrl.ext_mode, 0)
  poke(dut.io.ctrl.jmp_mode, 0)

  // JGEO
  poke(dut.io.ctrl.ext_mode, 1)
  poke(dut.io.ctrl.jmp_mode, 2)
  if (debug) {print("Testing JGEO")}
  for (i <- 1 to testSize) {
    a = start + r.nextInt((end - start) + 1)
    b = start + r.nextInt((a - start) + 1)
    c = -16 + r.nextInt((15 - -16) + 1)

    poke(dut.io.in.rs1, a)
    poke(dut.io.in.rs2, b)
    poke(dut.io.in.imm, c)
    result = c
    if (result < 0) {result = result + 65536}
    expect(dut.io.out.jump_amt, result)
    expect(dut.io.out.jump, 1)
  }
  poke(dut.io.ctrl.ext_mode, 0)
  poke(dut.io.ctrl.jmp_mode, 0)

  // JLEO
  poke(dut.io.ctrl.ext_mode, 1)
  poke(dut.io.ctrl.jmp_mode, 3)
  if (debug) {print("Testing JLEO")}
  for (i <- 1 to testSize) {
    b = start + r.nextInt((end - start) + 1)
    a = start + r.nextInt((b - start) + 1)
    c = -16 + r.nextInt((15 - -16) + 1)

    poke(dut.io.in.rs1, a)
    poke(dut.io.in.rs2, b)
    poke(dut.io.in.imm, c)
    result = c
    if (result < 0) {result = result + 65536}
    expect(dut.io.out.jump_amt, result)
    expect(dut.io.out.jump, 1)
  }
  poke(dut.io.ctrl.ext_mode, 0)
  poke(dut.io.ctrl.jmp_mode, 0)

  // JCO
  poke(dut.io.ctrl.alu_op, 1)
  step(1)

  poke(dut.io.ctrl.ext_mode, 1)
  poke(dut.io.ctrl.jmp_mode, 4)
  if (debug) {print("Testing JCO")}
  for (i <- 1 to testSize) {
    c = -16 + r.nextInt((15 - -16) + 1)

    poke(dut.io.in.imm, c)
    result = c
    if (result < 0) {result = result + 65536}
    expect(dut.io.out.jump_amt, result)
    expect(dut.io.out.jump, 1)
  }
  poke(dut.io.ctrl.ext_mode, 0)
  poke(dut.io.ctrl.jmp_mode, 0)

  // JEO
  poke(dut.io.ctrl.ext_mode, 1)
  poke(dut.io.ctrl.jmp_mode, 5)
  if (debug) {print("Testing JEO")}
  for (i <- 1 to testSize) {
    a = start + r.nextInt((end - start) + 1)
    c = -16 + r.nextInt((15 - -16) + 1)

    poke(dut.io.in.rs1, a)
    poke(dut.io.in.rs2, a)
    poke(dut.io.in.imm, c)
    result = c
    if (result < 0) {result = result + 65536}
    expect(dut.io.out.jump_amt, result)
    expect(dut.io.out.jump, 1)
  }
  poke(dut.io.ctrl.ext_mode, 0)

}

// test spec
class EXSpec extends FlatSpec with Matchers {
  "Tester" should "pass" in {
    chisel3.iotesters.Driver (() => new ExecuteTop()) { c =>
      new EXTester(c)
    } should be (true)
  }
}