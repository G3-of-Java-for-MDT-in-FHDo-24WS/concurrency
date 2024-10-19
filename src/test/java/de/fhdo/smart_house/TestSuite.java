package de.fhdo.smart_house;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

import de.fhdo.smart_house.service.CustomExceptionHandlerTest;
import de.fhdo.smart_house.service.DirectoryManagerTest;
import de.fhdo.smart_house.service.LogManageServiceTest;
import de.fhdo.smart_house.service.LogSearchServiceTest;

@Suite
@SuiteDisplayName("My Test Suite")
@SelectClasses({
    LogSearchServiceTest.class,
    LogManageServiceTest.class,
    DirectoryManagerTest.class,
    CustomExceptionHandlerTest.class
})

public class TestSuite {
    
}
