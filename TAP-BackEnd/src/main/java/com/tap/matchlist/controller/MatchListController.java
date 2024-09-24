package com.tap.matchlist.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tap.company.dto.CompanyDTO;
import com.tap.company.service.CompanyService;
import com.tap.detail.dto.SeatsDTO;
import com.tap.detail.service.SeatsService;
import com.tap.matchlist.dto.MatchListDTO;
import com.tap.matchlist.service.MatchListService;
import com.tap.members.dto.MembersDTO;
import com.tap.members.service.MembersService;

@RestController
@RequestMapping("/matchlist")
public class MatchListController {
    @Autowired
    private MatchListService mlServ;
    @Autowired
    private SeatsService sServ;
	@Autowired
	private MembersService mServ;
	@Autowired
	private CompanyService cServ;
	 @GetMapping("/baseball")
	    public ResponseEntity<Map<String, Object>> getBaseballMatches() {
	        List<MatchListDTO> matches;
	        List<SeatsDTO> seats = new ArrayList<>();
	        Map<String, Object> response = new HashMap<>();

	        try {
	            // 야구 경기 리스트를 가져옵니다.
	            matches = mlServ.getBaseballGames();

	            // 각 매치의 place_seq를 가져와서 좌석 가격을 조회합니다.
	            for (MatchListDTO match : matches) {
	                int placeSeq = match.getPlace_seq();
	                // 각 매치에 대한 좌석 가격을 가져와서 리스트에 추가합니다.
	                List<SeatsDTO> seatPrices = sServ.getPrice(placeSeq);
	                seats.addAll(seatPrices);
	            }

	            // 매치에 대한 회원 및 회사 정보를 가져옵니다.
	            List<MembersDTO> memberDataList = new ArrayList<>();
	            List<CompanyDTO> companyDataList = new ArrayList<>();

	            for (MatchListDTO match : matches) {
	                // 매치 ID를 사용하여 회원 및 회사 정보를 가져옵니다.
	                MembersDTO memberData = mServ.selectById(match.getId());
	                CompanyDTO companyData = cServ.getCompanyData(match.getId());

	                memberDataList.add(memberData);
	                companyDataList.add(companyData);
	            }

	            // 응답할 데이터 구조를 만듭니다.
	            response.put("matches", matches);
	            response.put("seats", seats);
	            response.put("members", memberDataList);
	            response.put("company", companyDataList);

	            return ResponseEntity.ok(response);
	        } catch (Exception e) {
	            e.printStackTrace(); // 예외 로그 출력
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	        }
	    }
    @GetMapping("/soccer")
    public ResponseEntity<List<MatchListDTO>> getSoccerMatches() {
        List<MatchListDTO> matches = mlServ.getSoccerGames();

        // 각 매치에 대해 좌석 정보를 추가
        for (MatchListDTO match : matches) {
            List<SeatsDTO> seats = sServ.getPrice(match.getPlace_seq());
            match.setSeatPrices(seats); // 매치 객체에 좌석 정보 추가
        }
        
        return ResponseEntity.ok(matches);
    }

}
