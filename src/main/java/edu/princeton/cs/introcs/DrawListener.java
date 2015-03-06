package edu.princeton.cs.introcs;

public interface DrawListener {
    public void mousePressed (double x, double y);
    public void mouseDragged (double x, double y);
    public void mouseReleased(double x, double y);
    public void keyTyped(char c);
}


/*************************************************************************
 *  Copyright 2002-2012, Robert Sedgewick and Kevin Wayne.
 *
 *  This file is part of stdlib-package.jar, which accompanies the textbook
 *
 *      Introduction to Programming in Java: An Interdisciplinary Approach
 *      by R. Sedgewick and K. Wayne, Addison-Wesley, 2007. ISBN 0-321-49805-4.
 *
 *      http://introcs.cs.princeton.edu
 *
 *
 *  stdlib-package.jar is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  stdlib-package.jar is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.

 *  You should have received a copy of the GNU General Public License
 *  along with stdlib-package.jar.  If not, see http://www.gnu.org/licenses.
 *************************************************************************/

