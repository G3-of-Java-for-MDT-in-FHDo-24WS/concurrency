package de.fhdo.smart_house;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

import de.fhdo.smart_house.service.LogSearchServiceTest;

@Suite
@SuiteDisplayName("My Test Suite")
@SelectClasses({
    LogSearchServiceTest.class
   
})

public class TestSuite {
    
}
