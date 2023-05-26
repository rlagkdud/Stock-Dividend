package com.dayone.service;

import com.dayone.model.Auth;
import com.dayone.persist.entity.MemberEntity;
import com.dayone.persist.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class MemberService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return memberRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("couldn't find usr er -> "+username));
    }

    public MemberEntity register(Auth.SignUp member){
        boolean exists = memberRepository.existsByUsername(member.getUsername());
        if(exists){
            throw new RuntimeException("이미 사용 중인 아이디 입니다.");
        }

        member.setPassword(this.passwordEncoder.encode(member.getPassword()));
        var result = memberRepository.save(member.toEntity());
        return result;
    }

    public MemberEntity authenticate(){
        return null;
    }
}
