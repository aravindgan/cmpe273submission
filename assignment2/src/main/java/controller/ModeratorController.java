package controller;

import domain.Moderator;
import domain.Poll;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by aravind on 2/27/15.
 */

@RestController
@RequestMapping("/api/v1")

public class ModeratorController {
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private ModeratorRepository moderatorRespository;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private PollRepository pollRepository;
    @Autowired
    KafkaProducer producer;

    private final AtomicInteger counter1 = new AtomicInteger();
    private final AtomicLong counter2 = new AtomicLong();
    private Moderator moderator1;
    private Integer id;
    private Poll poll1;
    private Long pollid;
    TimeZone timezone = TimeZone.getTimeZone("UTC");
    SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd'T'hh:mm:ss.SSS'T'");

    public boolean checkAuthentication(String authorization) throws UnsupportedEncodingException {
        String[] authorizationArray = authorization.split(" ");
        byte[] decodedString = Base64.decodeBase64(authorizationArray[1]);
        String authorizationString = new String(decodedString, "UTF-8");
        if (authorizationString.indexOf(":") > 0) {
            String[] credentials = authorizationString.split(":");
            String username = credentials[0];
            String password = credentials[1];
            if (username.equals("foo") && password.equals("bar"))
                return true;
        }
        return false;
    }

    @RequestMapping(value = "/moderators", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")

    public ResponseEntity<Moderator> createModerator(@RequestBody @Valid Moderator moderator) {
        //boolean isLogin = checkAuthentication(authentication1);
        //if(isLogin) {
        Moderator moderator1 = new Moderator();
        moderator1.setName(moderator.getName());
        id = (int) counter1.incrementAndGet();
        moderator1.setId(id);
        moderator1.setEmail(moderator.getEmail());
        moderator1.setPassword(moderator.getPassword());
        moderator1.setCreated_at(sdf.format(new Date()));
        moderatorRespository.save(moderator1);
        return new ResponseEntity<Moderator>(moderator1, HttpStatus.CREATED);
    } //else return null;


    @RequestMapping(value = "/moderators/{moderator_id}", method = RequestMethod.GET, headers = {"accept=application/json"})
    public
    @ResponseBody
    ResponseEntity<Moderator> moderator(@PathVariable("moderator_id") Integer id, @RequestHeader(value = "Authorization") String authentication1) throws UnsupportedEncodingException {
        boolean isLogin = checkAuthentication(authentication1);
        if (isLogin) {
            Moderator mod = moderatorRespository.findById(id);

            return new ResponseEntity<Moderator>(mod, HttpStatus.OK);
        }
        return new ResponseEntity<Moderator>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/moderators/{moderator_id}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")

    public ResponseEntity<Moderator> updateModerator(@PathVariable("moderator_id") Integer id2, @RequestBody Moderator moderator2, @RequestHeader(value = "Authorization") String authentication1) throws UnsupportedEncodingException {
        boolean isLogin = checkAuthentication(authentication1);
        if (isLogin) {
            Moderator mod1 = moderatorRespository.findById(id2);
            if (mod1 != null) {
                if (mod1.getId() == id2) {
                    if (moderator2.getName() != null)
                        mod1.setName(moderator2.getName());
                    if (moderator2.getEmail() != null)
                        mod1.setEmail(moderator2.getEmail());
                    if (moderator2.getPassword() != null)
                        mod1.setPassword(moderator2.getPassword());
                }
                moderatorRespository.save(mod1);
                return new ResponseEntity<Moderator>(mod1, HttpStatus.CREATED);
            }
        }
        return new ResponseEntity<Moderator>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/moderators/{moderator_id}/polls", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ResponseEntity<LinkedHashMap> createPoll(@PathVariable("moderator_id") Integer id3, @RequestHeader(value = "Authorization") String authentication1, @RequestBody Poll poll) throws UnsupportedEncodingException {
        LinkedHashMap pollhash = new LinkedHashMap();
        boolean isLogin = checkAuthentication(authentication1);
        if (isLogin) {
            //for (Moderator mod2 : Moderator.ModeratorList) {
            //if (mod2.getId() == id3) {
            poll1 = new Poll();
            poll1.setQuestion(poll.getQuestion());
            pollid = counter2.incrementAndGet();
            double rand = Math.random();
            pollid = (long) ((rand * 198756) + pollid);
            poll1.setId(Long.toHexString(pollid));
            poll1.setChoice(poll.getChoice());
            poll1.setModeratorId(id3);
            poll1.setStarted_at(poll.getStarted_at());
            poll1.setExpired_at(poll.getExpired_at());
            poll1.setExpired_at(poll.getExpired_at());
            int length = poll.getChoice().length;
            int[] tempArray = new int[length]; //tmpArray = {0,0,0}
            poll1.setResults(tempArray);
            pollRepository.save(poll1);
            Poll pollTemp = pollRepository.findById(poll1.getId());
            if (pollTemp != null) {
                pollhash.put("id", poll1.getId());
                pollhash.put("question", poll1.getQuestion());
                pollhash.put("started_at", poll1.getStarted_at());
                pollhash.put("expired_at", poll1.getExpired_at());
                pollhash.put("choice", poll1.getChoice());
                return new ResponseEntity<LinkedHashMap>(pollhash, HttpStatus.CREATED);
            }
            //mod2.PollList.add(poll1);
            //Moderator.PollGlobal.add(poll1);

        }
        return new ResponseEntity<LinkedHashMap>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/polls/{poll_id}", method = RequestMethod.GET)
    public ResponseEntity<LinkedHashMap> viewPollWithoutResult(@PathVariable("poll_id") String id) {
        LinkedHashMap pollhash1 = new LinkedHashMap();
        //for (Poll poll2 : Moderator.PollGlobal) {
        Poll poll2 = pollRepository.findById(id);
        //if (poll2.getId().toString().equals(id)) {
        if (poll2 != null) {
            pollhash1.put("id", poll2.getId());
            pollhash1.put("question", poll2.getQuestion());
            pollhash1.put("started_at", poll2.getStarted_at());
            pollhash1.put("expired_at", poll2.getExpired_at());
            pollhash1.put("choice", poll2.getChoice());
            return new ResponseEntity<LinkedHashMap>(pollhash1, HttpStatus.OK);
        }
        return null;
    }

    @RequestMapping(value = "/moderators/{moderator_id}/polls/{poll_id}", method = RequestMethod.GET)
    public ResponseEntity<Poll> poll(@PathVariable("moderator_id") Integer id, @PathVariable("poll_id") String id5, @RequestHeader(value = "Authorization") String authentication1) throws UnsupportedEncodingException {
        boolean isLogin = checkAuthentication(authentication1);
        if (isLogin) {
            //for (Moderator mod3 : Moderator.ModeratorList) {
            Poll pollTemp = pollRepository.findById(id5);
            //if (mod3.getId()== id) {
            //  for (Poll poll3 : mod3.PollList) {
            //    if (poll3.getId().toString().equals(id5)) {
            if (pollTemp != null && pollTemp.getModeratorId() == id) {
                return new ResponseEntity<Poll>(pollTemp, HttpStatus.OK);
            }
        }
        return new ResponseEntity<Poll>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/moderators/{moderator_id}/polls", method = RequestMethod.GET)
    public ResponseEntity<List<Poll>> viewAllpoll(@PathVariable("moderator_id") Integer id, @RequestHeader(value = "Authorization") String authentication1) throws UnsupportedEncodingException {
        boolean isLogin = checkAuthentication(authentication1);
        if (isLogin) {
            //for (Moderator mod4 : Moderator.ModeratorList) {
            List<Poll> pollList = pollRepository.findByModeratorId(id);
            //if (mod4.getId() == id) {
            if (pollList != null) {
                return new ResponseEntity<List<Poll>>(pollList, HttpStatus.OK);
            }
        }
        return new ResponseEntity<List<Poll>>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/moderators/{moderator_id}/polls/{poll_id}", method = RequestMethod.DELETE)
    public ResponseEntity deletepoll(@PathVariable("moderator_id") Integer id, @PathVariable("poll_id") String id6, @RequestHeader(value = "Authorization") String authentication1) throws UnsupportedEncodingException {

        boolean isLogin = checkAuthentication(authentication1);
        if (isLogin) {
            //for (Moderator mod5 : Moderator.ModeratorList) {
            //  if (mod5.getId() == id) {
            //    for (Poll poll3 : mod5.PollList) {
            //      if (poll3.getId().toString().equals(id6)) {
            //        mod5.PollList.remove(poll3);
            //      Moderator.PollGlobal.remove(poll3);
            if (pollRepository.findById(id6).getModeratorId() == id) {
                pollRepository.delete(id6);
                return new ResponseEntity(HttpStatus.NO_CONTENT);
            }
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/polls/{poll_id}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
    public ResponseEntity pollanswer(@PathVariable("poll_id") String id7, @RequestParam int choice) {
        //for (Poll poll7 : Moderator.PollGlobal) {
        //if (poll7.getId().toString().equals(id7)) {
        if (pollRepository.exists(id7)) {
            Poll pollTemp = pollRepository.findById(id7);
            pollTemp.getResults()[choice]++;
            pollRepository.save(pollTemp);
            //poll7.getResults()[choice]++;
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    public void checkForExpiredPolls() {
        ArrayList<Poll> pollList = (ArrayList<Poll>) pollRepository.findAll();
        Date dbDate = null;
        Date currentDate = new Date();

        for (Poll currentPoll : pollList) {
            String expiredDate = currentPoll.getExpired_at();
            Moderator moderator = moderatorRespository.findById(currentPoll.getModeratorId());

            try {
                dbDate = sdf.parse(expiredDate);
            } catch (ParseException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (currentDate.compareTo(dbDate) == 1) {
                System.out.print("\nPoll Expired of Moderator " + moderator.getEmail());
                if (!currentPoll.isEmailSent()) {

                    producer.sendExpiredMessage(moderator.getEmail(), currentPoll.getResults());
                    currentPoll.setEmailSent(true);
                    pollRepository.save(currentPoll);
                    System.out.print("Sending Mail");
                }
            }

        }
    }
}
