/*
 * Tester for the ALU
 *
 * Author: Dario Passarello
 *
 */

package lyng

import chisel3._
import chisel3.iotesters.PeekPokeTester
import lyng.execute.ALU
import org.scalatest.{FlatSpec, Matchers}
import scala.collection.immutable.ListMap
import chisel3.iotesters.Driver

import scala.util.Random

class MemoryTester(dut: Memory) extends PeekPokeTester(dut) {
    val populate = ListMap(0x0000 -> 0xFAA0,
                           0xFFFF -> 0xFBFC,
                           0xA000 -> 0x0102, 
                           0xA001 -> 0x0304, 
                           0xA002 -> 0x0506
                       )

    
    populate foreach {case (addr, value) =>
        poke(dut.io.addr, addr) 
        poke(dut.io.data_in, value)
        poke(dut.io.mem_write, 1)
        poke(dut.io.mem_read, 0)
        step(1)
        poke(dut.io.mem_write, 0)
        poke(dut.io.mem_read, 1)
        step(1)
        expect(dut.io.data_out, value, "Population error:")
    }

}


// test spec
class MemorySpec extends FlatSpec with Matchers {
  "Tester" should "pass" in {
      Driver.execute(Array("--generate-vcd-output", "on"), () => new Memory(16, 16)) {
          c => new MemoryTester(c)
      } should be (true)
  }
}