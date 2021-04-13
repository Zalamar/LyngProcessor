/*
 * Tester for the ALU
 *
 * Author: Tobias Rydberg (tobiasrydberg@pm.me)
 *
 */

package lyng

import chisel3._
import chisel3.iotesters.PeekPokeTester
import lyng.execute.ALU
import org.scalatest.{FlatSpec, Matchers}

import scala.util.Random

class ALUTester(dut: ALU) extends PeekPokeTester(dut) {

  val testSize = 20
  val r = new Random()
  val start = -32768
  val end = 32767

  var a = 0
  var b = 0
  var result = 0

  // ADD/ADDI
  for (i <- 1 to testSize) {
    a = start + r.nextInt((end - start) + 1)
    b = start + r.nextInt((end - start) + 1)

    poke(dut.io.alu_in1, a)
    poke(dut.io.alu_in2, b)
    poke(dut.io.alu_opcode, 2)
    step(1)
    result = a+b
    if (result > 32767) {result = result - 65536}
    else if (result < -32768) {result = result + 65536}
    expect(dut.io.alu_out, result)
  }

  // ADC
  /*
  val result = 10922
  poke(dut.io.alu_in1, 21845.S)
  poke(dut.io.alu_in2, 21845.S)
  poke(dut.io.alu_opcode, 0)
  step(1)
  expect(dut.io.carry_out, 1)
  expect(dut.io.alu_out, result)

  val result2 = 1021
  poke(dut.io.alu_in1, 382.S)
  poke(dut.io.alu_in2, 638.S)
  poke(dut.io.alu_opcode, 1)
  step(1)
  expect(dut.io.carry_out, 0)
  expect(dut.io.alu_out, result2)
   */

  // SUB
  for (i <- 1 to testSize) {
    a = start + r.nextInt((end - start) + 1)
    b = start + r.nextInt((end - start) + 1)

    poke(dut.io.alu_in1, a)
    poke(dut.io.alu_in2, b)
    poke(dut.io.alu_opcode, 4)
    step(4)
    result = a-b
    if (result > 32767) {result = result - 65536}
    else if (result < -32768) {result = result + 65536}
    expect(dut.io.alu_out, result)
  }

  // SBB

  // AND
  for (i <- 1 to testSize) {
    a = start + r.nextInt((end - start) + 1)
    b = start + r.nextInt((end - start) + 1)

    poke(dut.io.alu_in1, a)
    poke(dut.io.alu_in2, b)
    poke(dut.io.alu_opcode, 8)
    step(1)
    result = a&b
    expect(dut.io.alu_out, result)
  }

  // OR
  for (i <- 1 to testSize) {
    a = start + r.nextInt((end - start) + 1)
    b = start + r.nextInt((end - start) + 1)

    poke(dut.io.alu_in1, a)
    poke(dut.io.alu_in2, b)
    poke(dut.io.alu_opcode, 9)
    step(1)
    result = a|b
    expect(dut.io.alu_out, result)
  }

  // XOR
  for (i <- 1 to testSize) {
    a = start + r.nextInt((end - start) + 1)
    b = start + r.nextInt((end - start) + 1)

    poke(dut.io.alu_in1, a)
    poke(dut.io.alu_in2, b)
    poke(dut.io.alu_opcode, 10)
    step(1)
    result = a^b
    expect(dut.io.alu_out, result)
  }

  // NOT
  for (i <- 1 to testSize) {
    a = start + r.nextInt((end - start) + 1)

    poke(dut.io.alu_in1, a)
    poke(dut.io.alu_opcode, 11)
    step(1)
    result = ~a
    expect(dut.io.alu_out, result)
  }

  // SHIFTL
  for (i <- 1 to testSize) {
    a = 1 //start + r.nextInt((end - start) + 1)
    b = r.nextInt(15)

    poke(dut.io.alu_in1, a)
    poke(dut.io.alu_in1, b)
    poke(dut.io.alu_opcode, 12)
    step(1)
    result = a << b
    print("Test:", b, ";", result, ";")
    expect(dut.io.alu_out, result.toShort)
  }

  // SHIFTR
  for (i <- 1 to testSize) {
    a = 1 //start + r.nextInt((end - start) + 1)
    b = r.nextInt(15)

    poke(dut.io.alu_in1, a)
    poke(dut.io.alu_in1, b)
    poke(dut.io.alu_opcode, 13)
    step(1)
    result = a >> b
    expect(dut.io.alu_out, result)
  }

  // MVIH/MVIL
  for (i <- 1 to testSize) {
    a = start + r.nextInt((end - start) + 1)

    poke(dut.io.alu_in2, a)
    poke(dut.io.alu_opcode, 14)
    step(1)
    result = a
    expect(dut.io.alu_out, result)
  }

}

// test spec
class ALUSpec extends FlatSpec with Matchers {
  "Tester" should "pass" in {
    chisel3.iotesters.Driver (() => new ALU()) { c =>
      new ALUTester(c)
    } should be (true)
  }
}