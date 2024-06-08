package org.example.inminute_demo.zoom.auth;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ZoomTokenRepository extends JpaRepository<ZoomToken, Long> {
}