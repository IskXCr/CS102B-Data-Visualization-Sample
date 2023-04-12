package com.example.demo;

import com.example.demo.model.CountryInfoEntry;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

class DataManagerTest {
    @Test
    void testReloadData()
    {
        DataManager.reloadAllDataFromFile();
    }

    @Test
    void testFetchData()
    {
        DataManager.fetchLatestData();
    }
}