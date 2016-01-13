/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jna;

import com.sun.jna.Library;

/**
 *
 * @author Martin Drost <info@martindrost.nl>
 */
public interface ILibrary extends Library {
    void GetSystemTime(SYSTEMTIME lpSystemTime);
}
