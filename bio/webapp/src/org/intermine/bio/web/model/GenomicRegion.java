package org.intermine.bio.web.model;

/*
 * Copyright (C) 2002-2011 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

/**
 * This Java bean represents one record of Chromosome coordinates from user input
 * The record should be in BED format: "chr\tstart\tend".
 *
 * @author Fengyuan Hu
 */
public class GenomicRegion implements Comparable<GenomicRegion>
{
    private String chr;
    private Integer start;
    private Integer end;

    private Integer extendedStart;
    private Integer extendedEnd;
    private Integer extendedRegionSize; // user add region flanking

    // TODO should we add chromosome info to the model to make a genomic region unique?

    /**
     * Default constructor
     *
     * a new GenomicRegion must use setters to set start, end, extendedRegionSize
     */
    public GenomicRegion() {

    }
    /**
     * @return chr
     */
    public String getChr() {
        return chr;
    }

    /**
     * @param chr chromosome
     */
    public void setChr(String chr) {
        this.chr = chr;
    }

    /**
     * @return start
     */
    public Integer getStart() {
        return start;
    }

    /**
     * @param start start poistion
     */
    public void setStart(Integer start) {
        this.start = start;
    }

    /**
     * @return end
     */
    public Integer getEnd() {
        return end;
    }

    /**
     * @param end end position
     */
    public void setEnd(Integer end) {
        this.end = end;
    }

    /**
     * @return the extendedStart
     */
    public Integer getExtendedStart() {
        return extendedStart;
    }

    /**
     * @param extendedStart the extendedStart to set
     */
    public void setExtendedStart(Integer extendedStart) {
        this.extendedStart = extendedStart;
    }

    /**
     * @return the extendedEnd
     */
    public Integer getExtendedEnd() {
        return extendedEnd;
    }

    /**
     * @param extendedEnd the extendedEnd to set
     */
    public void setExtendedEnd(Integer extendedEnd) {
        this.extendedEnd = extendedEnd;
    }

    /**
     * @return the extendedRegionSize
     */
    public int getExtendedRegionSize() {
        return extendedRegionSize;
    }

    /**
     * @param extendedRegionSize the extendedRegionSize to set
     */
    public void setExtendedRegionSize(int extendedRegionSize) {
        this.extendedRegionSize = extendedRegionSize;
    }

    /**
     * Make a string of orginal region if extended
     * @return chr:start..end
     */
    public String getOriginalRegion() {
        return chr + ":" + start + ".." + end;
    }

    /**
     * @return chr:extendedStart..extenededEnd
     */
    public String getExtendedRegion() {
        if (extendedRegionSize == 0) {
            return getOriginalRegion();
        } else {
            return chr + ":" + extendedStart + ".." + extendedEnd;
        }
    }

    /**
     * @param obj a GenomicRegion object
     * @return boolean
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GenomicRegion) {
            GenomicRegion gr = (GenomicRegion) obj;

            if (extendedRegionSize == 0) {
                return (chr.equals(gr.getChr())
                        && start.equals(gr.getStart())
                        && end.equals(gr.getEnd()));
            } else {
                return (chr.equals(gr.getChr())
                        && start.equals(gr.getStart())
                        && end.equals(gr.getEnd())
                        && extendedStart.equals(gr.getExtendedStart())
                        && extendedEnd.equals(gr.getExtendedEnd())
                        && extendedRegionSize.equals(gr.getExtendedRegionSize()));
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return getOriginalRegion()
                + (getOriginalRegion().equals(getExtendedRegion()) ? ""
                        : " +/- " + extendedRegionSize);
    }

    /**
     * @return hashCode
     */
    @Override
    public int hashCode() {
        if (extendedRegionSize == 0) {
            return chr.hashCode() + start.hashCode() + end.hashCode();
        } else {
            return chr.hashCode() + start.hashCode() + end.hashCode()
                    + extendedStart.hashCode() + extendedEnd.hashCode()
                    + extendedRegionSize.hashCode();
        }
    }

    @Override
    public int compareTo(GenomicRegion gr) {
        final int bEFORE = -1;
        final int eQUAL = 0;
        final int aFTER = 1;

        //this optimization is usually worthwhile, and can
        //always be added
        if (this == gr) {
            return eQUAL;
        }

        if (this.getChr().compareTo(gr.getChr()) < 0) {
            return bEFORE;
        }

        if (this.getChr().equals(gr.getChr())) {
            if (extendedRegionSize == 0) {
                if (this.getStart() < gr.getStart()) {
                    return bEFORE;
                } else if (this.getStart() > gr.getStart()) {
                    return aFTER;
                } else {
                    if (this.getEnd() < gr.getEnd()) {
                        return bEFORE;
                    } else {
                        return aFTER;
                    }
                }
            } else {
                if (this.getExtendedStart() < gr.getExtendedStart()) {
                    return bEFORE;
                } else if (this.getExtendedStart() > gr.getExtendedStart()) {
                    return aFTER;
                } else {
                    if (this.getExtendedEnd() < gr.getExtendedEnd()) {
                        return bEFORE;
                    } else {
                        return aFTER;
                    }
                }
            }
        }


        return eQUAL;
    }
}
