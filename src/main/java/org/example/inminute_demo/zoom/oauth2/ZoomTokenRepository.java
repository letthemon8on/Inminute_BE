package org.example.inminute_demo.zoom.oauth2;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ZoomTokenRepository extends JpaRepository<ZoomToken, Long> {
}