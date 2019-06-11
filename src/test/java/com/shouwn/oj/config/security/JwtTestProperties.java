package com.shouwn.oj.config.security;

import com.shouwn.oj.security.JwtProperties;

public class JwtTestProperties implements JwtProperties {

	@Override
	public String getSecretKey() {
		return "032gj349uhb943hg93ug9040g39jg03jh03wi8jg908w230gh83hg032j0j3209hg3209h008j3w0g8uj0tjg098j3w490g8uj2048g9824jhg0824jg0ht0g8j2048g42g";
	}

	@Override
	public long getExpirationMs() {
		return 100000L;
	}
}
