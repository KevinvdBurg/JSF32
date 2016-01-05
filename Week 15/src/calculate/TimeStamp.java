/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calculate;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Martin
 */
public class TimeStamp {

    private static long counter = 0;
    private long curBegin;
    private String curBeginS;
    private List<Period> list;

    public TimeStamp() {
        calculate.TimeStamp.counter = 0;
        this.init();
    }

    /**
     * initialiseer klasse. begin met geen tijdsperiodes.
     */
    public void init() {
        this.curBegin = 0;
        this.curBeginS = null;
        this.list = new LinkedList<Period>();
    }

    /**
     * zet begintijdstip. gebruik interne teller voor identificatie van het
     * tijdstip
     */
    public void setBegin() {
        this.setBegin(String.valueOf(calculate.TimeStamp.counter++));
    }

    /**
     * zet begintijdstip
     *
     * @param timepoint betekenisvolle identificatie van begintijdstip
     */
    public void setBegin(String timepoint) {
        this.curBegin = System.currentTimeMillis();
        this.curBeginS = timepoint;
    }

    /**
     * zet eindtijdstip. gebruik interne teller voor identificatie van het
     * tijdstip
     */
    public void setEnd() {
        this.setEnd(String.valueOf(calculate.TimeStamp.counter++));
    }

    /**
     * zet eindtijdstip
     *
     * @param timepoint betekenisvolle identificatie vanhet eindtijdstip
     */
    public void setEnd(String timepoint) {
        this.list.add(new Period(this.curBegin, this.curBeginS, System.currentTimeMillis(), timepoint));
    }

    /**
     * zet eindtijdstip plus begintijdstip
     *
     * @param timepoint identificatie van het eind- en begintijdstip.
     */
    public void setEndBegin(String timepoint) {
        this.setEnd(timepoint);
        this.setBegin(timepoint);
    }

    /**
     * verkorte versie van setEndBegin
     *
     * @param timepoint
     */
    public void seb(String timepoint) {
        this.setEndBegin(timepoint);
    }

    /**
     * interne klasse voor bijhouden van periodes.
     *
     * @author erik
     *
     */
    private class Period {

        long begin;
        String beginS;
        long end;
        String endS;

        public Period(long b, String sb, long e, String se) {
            this.setBegin(b, sb);
            this.setEnd(e, se);
        }

        private void setBegin(long b, String sb) {
            this.begin = b;
            this.beginS = sb;
        }

        private void setEnd(long e, String se) {
            this.end = e;
            this.endS = se;
        }

        @Override
        public String toString() {
            return "From '" + this.beginS + "' till '" + this.endS + "' is " + (this.end - this.begin) + " mSec.";
        }
    }

    /**
     * override van toString methode. Geeft alle tijdsperiode weer.
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer();

        for (Period p : this.list) {
            buffer.append(p.toString());
            buffer.append('\n');
        }
        return buffer.toString();
    }
    
}
