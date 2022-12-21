package net.ict.bodymanager.repository.search;

import net.ict.bodymanager.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberSearch {

    Page<Member> search1(Pageable pageable);

    Page<Member> searchAll(String[] types, String keyword, Pageable pageable);
}
