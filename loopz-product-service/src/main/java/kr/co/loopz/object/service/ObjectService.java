package kr.co.loopz.object.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ObjectService {

    private final ObjectListService objectListService;
    private final ObjectDetailService objectDetailService;
    private final ObjectSearchService objectSearchService;




}
