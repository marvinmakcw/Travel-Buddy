package com.hkust.smart_buddy.common.util;

import lombok.experimental.UtilityClass;

import java.util.UUID;
import java.util.function.Predicate;

@UtilityClass
public class UuidUtil {
    
    /**
     * Generate a random UUID string
     * @return UUID string in format: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
     */
    public static String generateUuid() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * Generate a unique ID by checking if it already exists using the provided checker predicate
     * @param existsChecker predicate that returns true if the ID already exists
     * @return a unique UUID string that doesn't exist according to the checker
     */
    public static String generateUniqueId(Predicate<String> existsChecker) {
        while (true) {
            String candidateId = generateUuid();
            if (!existsChecker.test(candidateId)) {
                return candidateId;
            }
        }
    }
}