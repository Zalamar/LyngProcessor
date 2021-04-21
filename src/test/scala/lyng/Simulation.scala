// test spec
package lyng

import chisel3._
import chisel3.iotesters.PeekPokeTester
import org.scalatest.{FlatSpec, Matchers}
import chisel3.iotesters.Driver

class LyngTopSimulator(dut: LyngTop) extends PeekPokeTester(dut) {
    val instructions = List(
        0x1922,   //ADDI $1, $1, 2
        0x1922    //ADDI $1, $1, 2
    )

    //Load

    poke(dut.io.load, 1)
    for((instr, addr) <- instructions.zipWithIndex) {
        poke(dut.io.addr, addr << 1)
        poke(dut.io.value, instr)
        step(1)
    }
    step(1)
    poke(dut.io.load, 0)
    //Execute
    var end = false
    for(i <- 1 to 15) {
        val valid = peek(dut.io.out_valid)
        if(valid == 1) {
            println(peek(dut.io.out).toString)
        }
        step(1)
    }
}   




class LyngTopSimulatorSpec extends FlatSpec with Matchers {
  "Tester" should "pass" in {
      Driver.execute(Array("--generate-vcd-output", "on"), () => new LyngTop()) {
          c => new LyngTopSimulator(c)
      } should be (true)
  }
}