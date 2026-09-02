package com.example.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class DayFlowDateItem(
  val dateKey: String,       // e.g. "2026-09-01"
  val dayOfWeek: String,     // e.g. "MON"
  val dayNumber: String,     // e.g. "12"
  val isToday: Boolean,
  val isSelected: Boolean = false,
  val hasIndicator: Boolean = false
)

data class CalendarGridCell(
  val dateKey: String,
  val dayNumber: Int,
  val isCurrentMonth: Boolean,
  val isToday: Boolean,
  val isSelected: Boolean,
  val hasEvent: Boolean
)

object DateUtils {
  private val isoFormatThreadLocal = ThreadLocal.withInitial { SimpleDateFormat("yyyy-MM-dd", Locale.US) }
  private val dayOfWeekFormatThreadLocal = ThreadLocal.withInitial { SimpleDateFormat("EEE", Locale.US) }
  private val dayNumberFormatThreadLocal = ThreadLocal.withInitial { SimpleDateFormat("d", Locale.US) }
  private val displayFormatThreadLocal = ThreadLocal.withInitial { SimpleDateFormat("MMMM d", Locale.US) }
  private val monthYearFormatThreadLocal = ThreadLocal.withInitial { SimpleDateFormat("MMMM yyyy", Locale.US) }
  private val scheduleHeaderFormatThreadLocal = ThreadLocal.withInitial { SimpleDateFormat("MMM d", Locale.US) }
  private val timeFormatThreadLocal = ThreadLocal.withInitial { SimpleDateFormat("hh:mm a", Locale.US) }
  private val fullDateTimeFormatThreadLocal = ThreadLocal.withInitial { SimpleDateFormat("EEEE, MMMM d, yyyy 'at' hh:mm a", Locale.US) }

  private val isoFormat: SimpleDateFormat get() = isoFormatThreadLocal.get()!!
  private val dayOfWeekFormat: SimpleDateFormat get() = dayOfWeekFormatThreadLocal.get()!!
  private val dayNumberFormat: SimpleDateFormat get() = dayNumberFormatThreadLocal.get()!!
  private val displayFormat: SimpleDateFormat get() = displayFormatThreadLocal.get()!!
  private val monthYearFormat: SimpleDateFormat get() = monthYearFormatThreadLocal.get()!!
  private val scheduleHeaderFormat: SimpleDateFormat get() = scheduleHeaderFormatThreadLocal.get()!!
  private val timeFormat: SimpleDateFormat get() = timeFormatThreadLocal.get()!!
  private val fullDateTimeFormat: SimpleDateFormat get() = fullDateTimeFormatThreadLocal.get()!!

  fun getCurrentTimeFormatted(): String {
    return timeFormat.format(Date())
  }

  fun getFullCurrentDateTimeString(): String {
    return fullDateTimeFormat.format(Date())
  }

  fun getTodayDateKey(): String {
    return isoFormat.format(Date())
  }

  fun getCurrentYear(): Int {
    return Calendar.getInstance().get(Calendar.YEAR)
  }

  fun getCurrentMonth(): Int {
    return Calendar.getInstance().get(Calendar.MONTH)
  }

  fun getCurrentWeekDays(selectedDateKey: String = getTodayDateKey()): List<DayFlowDateItem> {
    val todayKey = getTodayDateKey()
    val calendar = Calendar.getInstance()

    // Find the Monday of current week
    calendar.firstDayOfWeek = Calendar.MONDAY
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

    val days = ArrayList<DayFlowDateItem>(7)
    for (i in 0 until 7) {
      val date = calendar.time
      val dateKey = isoFormat.format(date)
      val dayOfWeek = dayOfWeekFormat.format(date).uppercase(Locale.US)
      val dayNumber = dayNumberFormat.format(date)

      days.add(
        DayFlowDateItem(
          dateKey = dateKey,
          dayOfWeek = dayOfWeek,
          dayNumber = dayNumber,
          isToday = dateKey == todayKey,
          isSelected = dateKey == selectedDateKey,
          hasIndicator = false
        )
      )
      calendar.add(Calendar.DAY_OF_MONTH, 1)
    }
    return days
  }

  fun getMonthYearTitle(year: Int, month: Int): String {
    val cal = Calendar.getInstance()
    cal.set(Calendar.YEAR, year)
    cal.set(Calendar.MONTH, month)
    cal.set(Calendar.DAY_OF_MONTH, 1)
    return monthYearFormat.format(cal.time)
  }

  fun formatScheduleDate(dateKey: String): String {
    return try {
      val parsed = isoFormat.parse(dateKey)
      if (parsed != null) {
        scheduleHeaderFormat.format(parsed)
      } else {
        dateKey
      }
    } catch (e: Exception) {
      dateKey
    }
  }

  fun formatDisplayDate(dateKey: String): String {
    val today = getTodayDateKey()
    return try {
      val parsed = isoFormat.parse(dateKey)
      if (parsed != null) {
        if (dateKey == today) {
          "Today, ${displayFormat.format(parsed)}"
        } else {
          displayFormat.format(parsed)
        }
      } else {
        dateKey
      }
    } catch (e: Exception) {
      dateKey
    }
  }

  fun parseDate(dateKey: String): Date? {
    return try {
      isoFormat.parse(dateKey)
    } catch (e: Exception) {
      null
    }
  }

  fun buildMonthGrid(
    year: Int,
    month: Int,
    selectedDateKey: String,
    scheduledDateKeys: Set<String>
  ): List<List<CalendarGridCell>> {
    val todayKey = getTodayDateKey()
    val cal = Calendar.getInstance()
    cal.set(Calendar.YEAR, year)
    cal.set(Calendar.MONTH, month)
    cal.set(Calendar.DAY_OF_MONTH, 1)

    val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) // 1 = Sunday, 2 = Monday, ...
    val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

    // Calculate previous month's trailing days
    val prevMonthCal = Calendar.getInstance()
    prevMonthCal.set(Calendar.YEAR, year)
    prevMonthCal.set(Calendar.MONTH, month)
    prevMonthCal.add(Calendar.MONTH, -1)
    val daysInPrevMonth = prevMonthCal.getActualMaximum(Calendar.DAY_OF_MONTH)

    val leadingDaysCount = firstDayOfWeek - Calendar.SUNDAY // 0 if starts on Sunday

    val allCells = ArrayList<CalendarGridCell>(42)

    // 1. Leading days from previous month
    for (i in (daysInPrevMonth - leadingDaysCount + 1)..daysInPrevMonth) {
      prevMonthCal.set(Calendar.DAY_OF_MONTH, i)
      val cellDateKey = isoFormat.format(prevMonthCal.time)
      allCells.add(
        CalendarGridCell(
          dateKey = cellDateKey,
          dayNumber = i,
          isCurrentMonth = false,
          isToday = cellDateKey == todayKey,
          isSelected = cellDateKey == selectedDateKey,
          hasEvent = scheduledDateKeys.contains(cellDateKey)
        )
      )
    }

    // 2. Days of current month
    for (i in 1..daysInMonth) {
      cal.set(Calendar.DAY_OF_MONTH, i)
      val cellDateKey = isoFormat.format(cal.time)
      allCells.add(
        CalendarGridCell(
          dateKey = cellDateKey,
          dayNumber = i,
          isCurrentMonth = true,
          isToday = cellDateKey == todayKey,
          isSelected = cellDateKey == selectedDateKey,
          hasEvent = scheduledDateKeys.contains(cellDateKey)
        )
      )
    }

    // 3. Trailing days from next month to complete rows of 7
    val remainingDays = (7 - (allCells.size % 7)) % 7
    if (remainingDays > 0) {
      val nextMonthCal = Calendar.getInstance()
      nextMonthCal.set(Calendar.YEAR, year)
      nextMonthCal.set(Calendar.MONTH, month)
      nextMonthCal.add(Calendar.MONTH, 1)

      for (i in 1..remainingDays) {
        nextMonthCal.set(Calendar.DAY_OF_MONTH, i)
        val cellDateKey = isoFormat.format(nextMonthCal.time)
        allCells.add(
          CalendarGridCell(
            dateKey = cellDateKey,
            dayNumber = i,
            isCurrentMonth = false,
            isToday = cellDateKey == todayKey,
            isSelected = cellDateKey == selectedDateKey,
            hasEvent = scheduledDateKeys.contains(cellDateKey)
          )
        )
      }
    }

    return allCells.chunked(7)
  }

  fun formatDaysLeft(deadline: String): String {
    val trimmed = deadline.trim()
    if (trimmed.isBlank()) return ""
    if (trimmed.endsWith("left", ignoreCase = true) ||
      trimmed.equals("Due today", ignoreCase = true) ||
      trimmed.startsWith("Overdue", ignoreCase = true)
    ) {
      return trimmed
    }

    // Try parsing "yyyy-MM-dd"
    try {
      val parsedDate = isoFormat.parse(trimmed)
      if (parsedDate != null) {
        val nowCal = Calendar.getInstance()
        nowCal.set(Calendar.HOUR_OF_DAY, 0)
        nowCal.set(Calendar.MINUTE, 0)
        nowCal.set(Calendar.SECOND, 0)
        nowCal.set(Calendar.MILLISECOND, 0)

        val targetCal = Calendar.getInstance()
        targetCal.time = parsedDate
        targetCal.set(Calendar.HOUR_OF_DAY, 0)
        targetCal.set(Calendar.MINUTE, 0)
        targetCal.set(Calendar.SECOND, 0)
        targetCal.set(Calendar.MILLISECOND, 0)

        val diffMillis = targetCal.timeInMillis - nowCal.timeInMillis
        val diffDays = (diffMillis / (1000 * 60 * 60 * 24)).toInt()

        return when {
          diffDays > 1 -> "${diffDays}d left"
          diffDays == 1 -> "1d left"
          diffDays == 0 -> "Due today"
          else -> "Overdue (${-diffDays}d)"
        }
      }
    } catch (e: Exception) {
      // Ignore
    }

    // Check if numeric like "14" or "180"
    val numeric = trimmed.filter { it.isDigit() }.toIntOrNull()
    if (numeric != null && !trimmed.contains(" ")) {
      return "${numeric}d left"
    }

    return trimmed
  }

  fun getLastNDaysKeys(n: Int): List<String> {
    val cal = Calendar.getInstance()
    val list = ArrayList<String>(n)
    for (i in 0 until n) {
      list.add(isoFormat.format(cal.time))
      cal.add(Calendar.DAY_OF_MONTH, -1)
    }
    return list.reversed() // Oldest to newest (today at end)
  }

  fun getShortDayName(dateKey: String): String {
    return try {
      val parsed = isoFormat.parse(dateKey)
      if (parsed != null) {
        dayOfWeekFormat.format(parsed)
      } else {
        dateKey
      }
    } catch (e: Exception) {
      dateKey
    }
  }

  fun getDayOfMonthString(dateKey: String): String {
    return try {
      val parsed = isoFormat.parse(dateKey)
      if (parsed != null) {
        dayNumberFormat.format(parsed)
      } else {
        dateKey
      }
    } catch (e: Exception) {
      dateKey
    }
  }

  fun formatFocusMinutes(minutes: Int): String {
    if (minutes <= 0) return "0m"
    val hours = minutes / 60
    val remainingMinutes = minutes % 60
    return when {
      hours > 0 && remainingMinutes > 0 -> "${hours}h ${remainingMinutes}m"
      hours > 0 -> "${hours}h"
      else -> "${remainingMinutes}m"
    }
  }

  fun formatIso(date: Date): String {
    return isoFormat.format(date)
  }

  fun getDateKeyOffset(baseDateKey: String, offsetDays: Int): String {
    val parsed = parseDate(baseDateKey) ?: Date()
    val cal = Calendar.getInstance()
    cal.time = parsed
    cal.add(Calendar.DAY_OF_MONTH, offsetDays)
    return isoFormat.format(cal.time)
  }

  /**
   * Dynamically calculates current streak and best streak from the set of active completed dates.
   * A streak is counted on consecutive calendar days.
   */
  fun calculateStreak(completedDates: Set<String>, todayKey: String = getTodayDateKey()): Pair<Int, Int> {
    if (completedDates.isEmpty()) return Pair(0, 0)
    val cal = Calendar.getInstance()
    val parsedToday = parseDate(todayKey) ?: Date()
    cal.time = parsedToday

    // 1. Calculate current streak
    var currentStreak = 0
    val isTodayCompleted = completedDates.contains(todayKey)
    if (isTodayCompleted) {
      currentStreak++
      cal.add(Calendar.DAY_OF_MONTH, -1)
      while (completedDates.contains(isoFormat.format(cal.time))) {
        currentStreak++
        cal.add(Calendar.DAY_OF_MONTH, -1)
      }
    } else {
      // Check if yesterday was completed
      cal.add(Calendar.DAY_OF_MONTH, -1)
      val yesterdayKey = isoFormat.format(cal.time)
      if (completedDates.contains(yesterdayKey)) {
        currentStreak++
        cal.add(Calendar.DAY_OF_MONTH, -1)
        while (completedDates.contains(isoFormat.format(cal.time))) {
          currentStreak++
          cal.add(Calendar.DAY_OF_MONTH, -1)
        }
      }
    }

    // 2. Calculate all-time best streak
    val sortedDates = completedDates.mapNotNull { parseDate(it) }.sorted()
    var maxStreak = 0
    var runningStreak = 0
    var lastTimeMillis: Long = -1L
    val oneDayMillis = 24 * 60 * 60 * 1000L

    for (date in sortedDates) {
      val timeMillis = date.time
      if (lastTimeMillis == -1L) {
        runningStreak = 1
      } else {
        val diffDays = Math.round((timeMillis - lastTimeMillis).toDouble() / oneDayMillis).toInt()
        if (diffDays == 1) {
          runningStreak++
        } else if (diffDays > 1) {
          runningStreak = 1
        }
      }
      if (runningStreak > maxStreak) {
        maxStreak = runningStreak
      }
      lastTimeMillis = timeMillis
    }

    val bestStreak = maxOf(maxStreak, currentStreak)
    return Pair(currentStreak, bestStreak)
  }
}
