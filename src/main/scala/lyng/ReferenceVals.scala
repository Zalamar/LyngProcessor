package lyng

import chisel3._
import chisel3.util._
import scala.collection.immutable.ListMap

object RefernceVals {

  // refernce list of opcodes
  val op_ref = ListMap("ADD"   -> "b00000".U,
                       "ADC"   -> "b00000".U,
                       "SUB"   -> "b00000".U,
                       "SBB"   -> "b00000".U,
                       "AND"   -> "b00001".U,
                       "OR"    -> "b00001".U,
                       "XOR"   -> "b00001".U,
                       "NOT"   -> "b00001".U,
                       "SHFL"  -> "b00010".U,
                       "SHFA"  -> "b00010".U,
                       "ADDI"  -> "b00011".U,
                       "SUBI"  -> "b00100".U,
                       "MVIH"  -> "b00101".U,
                       "MVIL"  -> "b00110".U,
                       "LDIDR" -> "b00111".U,
                       "STIDR" -> "b01000".U,
                       "LDIDX" -> "b01001".U,
                       "STIDX" -> "b01010".U,
                       "JMP"   -> "b01011".U,
                       "JMPI"  -> "b01100".U,
                       "JMPI"  -> "b01100".U,
                       "JGEO"  -> "b01101".U,
                       "JLEO"  -> "b01110".U,
                       "JCO"   -> "b01111".U,
                       "JEO"   -> "b10000".U,
                       "PUSH"  -> "b10001".U,
                       "POP"   -> "b10010".U,
                       "CALL"  -> "b10011".U,
                       "JAL"   -> "b10100".U,
                       "MOVSP" -> "b10101".U,
                       "RET"   -> "b10110".U,
                       "STC"   -> "b10111".U
  )

  // reference list of func codes
  val func_ref = ListMap("ADD"   -> "b00".U,
                         "ADC"   -> "b01".U,
                         "SUB"   -> "b10".U,
                         "SBB"   -> "b11".U,
                         "AND"   -> "b00".U,
                         "OR"    -> "b01".U,
                         "XOR"   -> "b10".U,
                         "NOT"   -> "b11".U,
                         "SHFL"  -> "b00".U,
                         "SHFA"  -> "b01".U,
                         "ADDI"  -> "b00".U,
                         "SUBI"  -> "b00".U,
                         "MVIH"  -> "b00".U,
                         "MVIL"  -> "b00".U,
                         "LDIDR" -> "b00".U,
                         "STIDR" -> "b00".U,
                         "LDIDX" -> "b00".U,
                         "STIDX" -> "b00".U,
                         "JMP"   -> "b00".U,
                         "JMPI"  -> "b00".U,
                         "JMPI"  -> "b00".U,
                         "JGEO"  -> "b00".U,
                         "JLEO"  -> "b00".U,
                         "JCO"   -> "b00".U,
                         "JEO"   -> "b00".U,
                         "PUSH"  -> "b00".U,
                         "POP"   -> "b00".U,
                         "CALL"  -> "b00".U,
                         "JAL"   -> "b00".U,
                         "MOVSP" -> "b00".U,
                         "RET"   -> "b00".U,
                         "STC"   -> "b00".U
  )

}
