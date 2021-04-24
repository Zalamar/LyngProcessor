// test spec
package lyng

import chisel3._
import chisel3.iotesters.PeekPokeTester
import org.scalatest.{FlatSpec, Matchers}
import scala.io.Source
import chisel3.iotesters.Driver
import java.io.{BufferedInputStream, FileInputStream}

class LyngTopSimulator(dut: LyngTop) extends PeekPokeTester(dut) {

    //val instructions = Source.fromURL(getClass.getResource("/code.txt")).getLines.toList.map(x => Integer.parseInt(x.slice(0,4), 16)) 

    

    val bis = new BufferedInputStream(getClass().getResourceAsStream("/program.bin"))
    val bArray = Stream.continually(bis.read).takeWhile(-1 !=).map(_.toByte).toList
    print(bArray)
    println()
    print(bArray.length)
    val instructions = bArray.drop(1).zip(bArray).zipWithIndex
        .filter { case ((x, y), i) => i % 2 == 0 }
        .map { case ((x, y), _) => (y << 8) + x }
        .toList

    print(instructions)

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
    for(i <- 1 to 100) {
        val valid = peek(dut.io.out_valid)
        if(valid == 1) {
            println(peek(dut.io.out).toString)
        }
        step(1)
    }
}   




class Simulator extends FlatSpec with Matchers {
  "Tester" should "pass" in {
      Driver.execute(Array("--generate-vcd-output", "on"), () => new LyngTop()) {
          c => new LyngTopSimulator(c)
      } should be (true)
  }
}