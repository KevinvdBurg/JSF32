/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jna;

import com.sun.jna.Library;
import com.sun.jna.ptr.IntByReference;

/**
 *
 * @author Martin Drost <info@martindrost.nl>
 */
public interface ILibrary extends Library {
    void GetSystemTime(SYSTEMTIME lpSystemTime);
    boolean GetDiskFreeSpaceA(String lpRootPathName, IntByReference lpSectorsPerCluster, IntByReference lpBytesPerSector, IntByReference lpNumberOfFreeClusters, IntByReference lpTotalNumberOfClusters);
    
}

