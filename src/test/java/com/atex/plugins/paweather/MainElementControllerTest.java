package com.atex.plugins.paweather;

import junit.framework.TestCase;

public class MainElementControllerTest extends TestCase {
    
    private MainElementController _testTarget;
    
    @Override
    protected void setUp() {
        _testTarget = new MainElementController();
    }
     
    
    public void testHelloGreenfieldTimesControllerConstruction()
        throws Exception
    {
        assertNotNull("controller failed initialization", _testTarget);
    }
}
