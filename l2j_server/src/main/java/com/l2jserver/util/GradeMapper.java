package com.l2jserver.util;


public class GradeMapper {

  public static String resolveGradeString(int level) {
    return resolveGrade(level).gradeSymbol();
  }

  public static Grade resolveGrade(int level) {
    if (level >= 1 && level < 20) {
      return Grade.NG;
    } else if (level >= 20 && level < 40) {
      return Grade.D;
    } else if (level >= 40 && level < 52) {
      return Grade.C;
    } else if (level >= 52 && level < 61) {
      return Grade.B;
    } else if (level >= 61 && level < 76) {
      return Grade.A;
    } else if (level >= 76 && level < 80) {
      return Grade.S;
    } else if (level >= 80) {
      return Grade.S_PLUS;
    } else {
      return Grade.UNKNOWN;
    }
  }

}
