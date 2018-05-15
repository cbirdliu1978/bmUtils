/// **
// * The MIT License (MIT)
// *
// * Copyright (c) 2014-2017 Marc de Verdelhan & respective authors (see AUTHORS)
// *
// * Permission is hereby granted, free of charge, to any person obtaining a copy of
// * this software and associated documentation files (the "Software"), to deal in
// * the Software without restriction, including without limitation the rights to
// * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
// * the Software, and to permit persons to whom the Software is furnished to do so,
// * subject to the following conditions:
// *
// * The above copyright notice and this permission notice shall be included in all
// * copies or substantial portions of the Software.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
// * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
// * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
// * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
// * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
// */
// package eu.verdelhan.ta4j;
//
// import java.io.Serializable;
//
// import org.joda.time.DateTime;
// import org.joda.time.Period;
//
/// **
// * End tick of a time period.
// * <p>
// */
// public abstract class Tick2 implements Serializable {
//
// private static final long serialVersionUID = 8038383777467488147L;
//
// /**
// * @return the close price of the period
// */
// public abstract Decimal getClosePrice();
//
// /**
// * @return the open price of the period
// */
// public abstract Decimal getOpenPrice();
//
// /**
// * @return the number of trades in the period
// */
// public abstract int getTrades();
//
// /**
// * @return the max price of the period
// */
// public abstract Decimal getMaxPrice();
//
// /**
// * @return the whole traded amount of the period
// */
// public abstract Decimal getAmount();
//
// /**
// * @return the whole traded volume in the period
// */
// public abstract Decimal getVolume();
//
// /**
// * @return the min price of the period
// */
// public abstract Decimal getMinPrice();
//
// /**
// * @return the time period of the tick
// */
// public abstract Period getTimePeriod();
//
// /**
// * @return the begin timestamp of the tick period
// */
// public abstract DateTime getBeginTime();
//
// /**
// * @return the end timestamp of the tick period
// */
// public abstract DateTime getEndTime();
//
// /**
// * @param timestamp
// * a timestamp
// * @return true if the provided timestamp is between the begin time and the end time of the current period, false otherwise
// */
// public boolean inPeriod(DateTime timestamp) {
// return timestamp != null && !timestamp.isBefore(getBeginTime()) && timestamp.isBefore(getEndTime());
// }
//
// /**
// * @return true if this is a bearish tick, false otherwise
// */
// public boolean isBearish() {
// return (getOpenPrice() != null) && (getClosePrice() != null) && getClosePrice().isLessThan(getOpenPrice());
// }
//
// /**
// * @return true if this is a bullish tick, false otherwise
// */
// public boolean isBullish() {
// return (getOpenPrice() != null) && (getClosePrice() != null) && getOpenPrice().isLessThan(getClosePrice());
// }
//
// /**
// * @return a human-friendly string of the end timestamp
// */
// public String getDateName() {
// return getEndTime().toString("hh:mm dd/MM/yyyy");
// }
//
// /**
// * @return a even more human-friendly string of the end timestamp
// */
// public String getSimpleDateName() {
// return getEndTime().toString("dd/MM/yyyy");
// }
//
// /**
// * @param timePeriod
// * the time period
// * @param endTime
// * the end time of the tick
// * @throws IllegalArgumentException
// * if one of the arguments is null
// */
// protected void checkTimeArguments(Period timePeriod, DateTime endTime) {
// if (timePeriod == null) {
// throw new IllegalArgumentException("Time period cannot be null");
// }
// if (endTime == null) {
// throw new IllegalArgumentException("End time cannot be null");
// }
// }
// }
