package com.example.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Bedtime
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Brush
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.CleaningServices
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Flight
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.model.ItemCategory

object CategoryIconHelper {

  data class IconOption(
    val id: String,
    val label: String,
    val icon: ImageVector
  )

  val availableIcons: List<IconOption> = listOf(
    IconOption("work", "Work", Icons.Outlined.WorkOutline),
    IconOption("person", "Personal", Icons.Outlined.Person),
    IconOption("health", "Health", Icons.Outlined.FavoriteBorder),
    IconOption("school", "Learning", Icons.Outlined.School),
    IconOption("fitness", "Fitness", Icons.Outlined.FitnessCenter),
    IconOption("mind", "Mind", Icons.Outlined.Spa),
    IconOption("book", "Reading", Icons.Outlined.MenuBook),
    IconOption("finance", "Finance", Icons.Outlined.AccountBalanceWallet),
    IconOption("family", "Family", Icons.Outlined.People),
    IconOption("travel", "Travel", Icons.Outlined.Flight),
    IconOption("shopping", "Shopping", Icons.Outlined.ShoppingCart),
    IconOption("project", "Projects", Icons.Outlined.Assignment),
    IconOption("meeting", "Meetings", Icons.Outlined.Groups),
    IconOption("sleep", "Sleep", Icons.Outlined.Bedtime),
    IconOption("code", "Coding", Icons.Outlined.Code),
    IconOption("palette", "Design", Icons.Outlined.Palette),
    IconOption("edit", "Writing", Icons.Outlined.Edit),
    IconOption("home", "Home", Icons.Outlined.Home),
    IconOption("cleaning", "Chores", Icons.Outlined.CleaningServices),
    IconOption("restaurant", "Nutrition", Icons.Outlined.Restaurant),
    IconOption("brush", "Creative", Icons.Outlined.Brush),
    IconOption("bolt", "Productivity", Icons.Outlined.Bolt),
    IconOption("chat", "Social", Icons.Outlined.Forum),
    IconOption("trending", "Career", Icons.Outlined.TrendingUp),
    IconOption("music", "Music", Icons.Outlined.MusicNote),
    IconOption("schedule", "Routine", Icons.Outlined.Schedule),
    IconOption("star", "Important", Icons.Outlined.StarBorder),
    IconOption("flag", "Goal", Icons.Outlined.Flag),
    IconOption("lightbulb", "Idea", Icons.Outlined.Lightbulb),
    IconOption("category", "Other", Icons.Outlined.Category)
  )

  fun getIconForCategory(category: ItemCategory): ImageVector {
    return when (category) {
      ItemCategory.WORK -> Icons.Outlined.WorkOutline
      ItemCategory.PERSONAL -> Icons.Outlined.Person
      ItemCategory.HEALTH -> Icons.Outlined.FavoriteBorder
      ItemCategory.LEARNING -> Icons.Outlined.School
      ItemCategory.FITNESS -> Icons.Outlined.FitnessCenter
      ItemCategory.MINDFULNESS -> Icons.Outlined.Spa
      ItemCategory.STUDY -> Icons.Outlined.MenuBook
      ItemCategory.READING -> Icons.Outlined.Book
      ItemCategory.FINANCE -> Icons.Outlined.AccountBalanceWallet
      ItemCategory.FAMILY -> Icons.Outlined.People
      ItemCategory.TRAVEL -> Icons.Outlined.Flight
      ItemCategory.SHOPPING -> Icons.Outlined.ShoppingCart
      ItemCategory.PROJECTS -> Icons.Outlined.Assignment
      ItemCategory.MEETINGS -> Icons.Outlined.Groups
      ItemCategory.SLEEP -> Icons.Outlined.Bedtime
      ItemCategory.CODING -> Icons.Outlined.Code
      ItemCategory.DESIGN -> Icons.Outlined.Palette
      ItemCategory.WRITING -> Icons.Outlined.Edit
      ItemCategory.HOME -> Icons.Outlined.Home
      ItemCategory.CHORES -> Icons.Outlined.CleaningServices
      ItemCategory.NUTRITION -> Icons.Outlined.Restaurant
      ItemCategory.MEDITATION -> Icons.Outlined.Spa
      ItemCategory.CREATIVE -> Icons.Outlined.Brush
      ItemCategory.PRODUCTIVITY -> Icons.Outlined.Bolt
      ItemCategory.SOCIAL -> Icons.Outlined.Forum
      ItemCategory.CAREER -> Icons.Outlined.TrendingUp
      ItemCategory.MUSIC -> Icons.Outlined.MusicNote
      ItemCategory.ROUTINE -> Icons.Outlined.Schedule
      ItemCategory.OTHER -> Icons.Outlined.Category
    }
  }

  fun getIconByName(iconName: String): ImageVector {
    return availableIcons.firstOrNull { it.id.equals(iconName, ignoreCase = true) }?.icon
      ?: Icons.Outlined.Category
  }

  /**
   * AI/Semantic Auto-Icon selector:
   * Maps a category name or keyword into the appropriate monochrome outline Material icon.
   * Never generates emojis or colorful graphics; always stays strictly within DayFlow's outline vocabulary.
   */
  fun inferIconForName(name: String): String {
    val lower = name.lowercase().trim()
    return when {
      lower.contains("code") || lower.contains("dev") || lower.contains("prog") || lower.contains("app") || lower.contains("tech") || lower.contains("software") -> "code"
      lower.contains("study") || lower.contains("course") || lower.contains("exam") || lower.contains("class") || lower.contains("learn") || lower.contains("lesson") -> "school"
      lower.contains("book") || lower.contains("read") || lower.contains("novel") || lower.contains("paper") -> "book"
      lower.contains("money") || lower.contains("finance") || lower.contains("budget") || lower.contains("bank") || lower.contains("tax") || lower.contains("crypto") || lower.contains("invest") -> "finance"
      lower.contains("gym") || lower.contains("workout") || lower.contains("run") || lower.contains("fitness") || lower.contains("exercise") || lower.contains("training") || lower.contains("walk") -> "fitness"
      lower.contains("health") || lower.contains("doctor") || lower.contains("med") || lower.contains("pill") || lower.contains("dentist") -> "health"
      lower.contains("meditat") || lower.contains("mind") || lower.contains("breath") || lower.contains("zen") || lower.contains("yoga") || lower.contains("relax") -> "mind"
      lower.contains("family") || lower.contains("kid") || lower.contains("parent") || lower.contains("child") || lower.contains("baby") -> "family"
      lower.contains("friend") || lower.contains("social") || lower.contains("party") || lower.contains("chat") || lower.contains("dinner") || lower.contains("meetup") -> "chat"
      lower.contains("meet") || lower.contains("sync") || lower.contains("standup") || lower.contains("call") || lower.contains("zoom") || lower.contains("client") -> "meeting"
      lower.contains("travel") || lower.contains("trip") || lower.contains("flight") || lower.contains("vacation") || lower.contains("hotel") || lower.contains("visit") -> "travel"
      lower.contains("shop") || lower.contains("buy") || lower.contains("grocer") || lower.contains("store") || lower.contains("market") || lower.contains("order") -> "shopping"
      lower.contains("project") || lower.contains("task") || lower.contains("sprint") || lower.contains("deliver") || lower.contains("milestone") -> "project"
      lower.contains("design") || lower.contains("art") || lower.contains("sketch") || lower.contains("figma") || lower.contains("ui") || lower.contains("ux") || lower.contains("graphic") -> "palette"
      lower.contains("write") || lower.contains("blog") || lower.contains("post") || lower.contains("essay") || lower.contains("journal") || lower.contains("note") -> "edit"
      lower.contains("home") || lower.contains("house") || lower.contains("garden") || lower.contains("plant") || lower.contains("yard") || lower.contains("rent") -> "home"
      lower.contains("clean") || lower.contains("chore") || lower.contains("laundry") || lower.contains("dish") || lower.contains("tidy") || lower.contains("wash") -> "cleaning"
      lower.contains("food") || lower.contains("eat") || lower.contains("cook") || lower.contains("diet") || lower.contains("nutrition") || lower.contains("meal") || lower.contains("recipe") -> "restaurant"
      lower.contains("sleep") || lower.contains("rest") || lower.contains("nap") || lower.contains("bed") -> "sleep"
      lower.contains("music") || lower.contains("piano") || lower.contains("guitar") || lower.contains("song") || lower.contains("play") || lower.contains("audio") -> "music"
      lower.contains("career") || lower.contains("job") || lower.contains("interview") || lower.contains("resume") || lower.contains("promote") || lower.contains("growth") -> "trending"
      lower.contains("idea") || lower.contains("brainstorm") || lower.contains("inspire") || lower.contains("plan") -> "lightbulb"
      lower.contains("goal") || lower.contains("target") || lower.contains("achieve") || lower.contains("aim") -> "flag"
      lower.contains("focus") || lower.contains("streak") || lower.contains("speed") || lower.contains("fast") || lower.contains("energy") -> "bolt"
      lower.contains("creative") || lower.contains("craft") || lower.contains("draw") || lower.contains("paint") || lower.contains("photo") -> "brush"
      lower.contains("routine") || lower.contains("daily") || lower.contains("morning") || lower.contains("evening") || lower.contains("night") -> "schedule"
      lower.contains("work") || lower.contains("office") || lower.contains("corp") || lower.contains("business") -> "work"
      else -> "category"
    }
  }
}
