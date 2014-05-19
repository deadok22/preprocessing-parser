/*
 * xtc - The eXTensible Compiler
 * Copyright (C) 2009-2012 New York University
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301,
 * USA.
 */
package ru.spbau.preprocessing.xtc.erlang;

import xtc.lang.cpp.Actions;

/**
 * This class is generated from grammar annotations and provides semantic
 * value and action support.
 */
public class ErlangActionsBase extends Actions {

  public ValueType getValueType(int id) {
    if (0 <= id && id < 303 || -1 < id) {
      return ValueType.NODE;
    }
    switch (id - 303) {
      default:
        return ValueType.NODE;
    }
  }
  public boolean isComplete(int id) {
    switch(id) {
    default:
      return false;
    }
  }

}
