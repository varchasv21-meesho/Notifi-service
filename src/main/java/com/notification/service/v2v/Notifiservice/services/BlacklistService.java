package com.notification.service.v2v.Notifiservice.services;

import com.notification.service.v2v.Notifiservice.dao.BlacklistRepository;
import com.notification.service.v2v.Notifiservice.entity.BlacklistEntity;
import com.notification.service.v2v.Notifiservice.exceptionHandling.ErrorResponse;
import com.notification.service.v2v.Notifiservice.exceptionHandling.ValidationException;
import com.notification.service.v2v.Notifiservice.validators.SmsRequestValidator;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Slf4j
public class BlacklistService {

    private final BlacklistRepository blacklistRepository;

    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public BlacklistService(BlacklistRepository blacklistRepository, RedisTemplate<String, String> redisTemplate){
        this.blacklistRepository = blacklistRepository;
        this.redisTemplate = redisTemplate;
    }

    public Set<String> getBlacklist() throws ValidationException {
        Set<String> getBlacklistedNumbers;
        try {
            getBlacklistedNumbers = redisTemplate.opsForSet().members("blacklist");
        }catch (Exception e){
            log.debug("Redis threw error: opsForSet() function");
            throw new ValidationException("Redis threw error");
        }
        return getBlacklistedNumbers;
    }

    @Transactional
    public void addToBlacklist(@NotNull Set<String> phoneNumbers) throws ValidationException {

        // Validating set of Phone Numbers
        if(SmsRequestValidator.checkAllPhoneNumber(phoneNumbers))
            throw new ValidationException(new ErrorResponse(400, "invalid formatting of phone numbers"));
        try {
            // Adding into Redis
            redisTemplate.opsForSet().add("blacklist", phoneNumbers.toArray(new String[0]));

            // Adding in MySQL
            for (String phoneNumber : phoneNumbers) {
                BlacklistEntity entity = new BlacklistEntity();
                entity.setPhoneNumber(phoneNumber.substring(Math.max(0,phoneNumber.length()-10)));
//                entity.setPhoneNumber(phoneNumber);
                blacklistRepository.save(entity);
            }
        } catch (Exception e) {
            log.debug("Redis threw error: opsForSet() function");
            throw new ValidationException("Redis threw error");
        }
    }

    @Transactional
    public void removeFromBlacklist(Set<String> phoneNumbers) throws ValidationException {
        // Validating set of Phone Numbers
        if(SmsRequestValidator.checkAllPhoneNumber(phoneNumbers))
            throw new ValidationException(new ErrorResponse(400, "invalid formatting of phone numbers"));
        try {
            // Remove from Redis
            redisTemplate.opsForSet().remove("blacklist", phoneNumbers.toArray(new String[0]));

            // Remove from MySQL
            for(String phoneNumber : phoneNumbers){
                blacklistRepository.deleteByPhoneNumber(phoneNumber);
            }
        }
        catch (Exception e) {
            log.debug("Redis threw error: opsForSet() function");
            throw new ValidationException("Redis threw error");
        }


    }

    public boolean isBlacklisted(String phoneNumber) {
        return redisTemplate.opsForSet().isMember("blacklist", phoneNumber) == Boolean.TRUE;
    }
}
