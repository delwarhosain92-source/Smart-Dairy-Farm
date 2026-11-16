package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AdminPanelSettings
import androidx.compose.material.icons.rounded.Agriculture
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.preferences.UserRole
import com.example.ui.theme.AlertCoral
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.CoralContainer
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.LocalAppLanguage
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.MilkSky
import com.example.ui.theme.MilkSkyContainer
import com.example.ui.theme.MintBorder
import com.example.ui.theme.MintSoft
import com.example.ui.theme.WarmAmber

@Composable
fun FarmTopBar(
  title: String,
  userRole: UserRole,
  onToggleLanguage: () -> Unit,
  onOpenSettings: () -> Unit,
  modifier: Modifier = Modifier,
  showBackButton: Boolean = false,
  onBackClick: () -> Unit = {},
) {
  val strings = LocalAppStrings.current
  val language = LocalAppLanguage.current

  Surface(
    color = MaterialTheme.colorScheme.surface,
    tonalElevation = 1.dp,
    shadowElevation = 2.dp,
    modifier = modifier.fillMaxWidth(),
  ) {
    Row(
      modifier = Modifier
        .statusBarsPadding()
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(1f),
      ) {
        if (showBackButton) {
          IconButton(
            onClick = onBackClick,
            modifier = Modifier
              .minimumInteractiveComponentSize()
              .testTag("top_bar_back_button"),
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
              contentDescription = strings.close,
              tint = MaterialTheme.colorScheme.primary,
            )
          }
          Spacer(modifier = Modifier.width(4.dp))
        } else {
          Surface(
            shape = CircleShape,
            color = MintSoft,
            modifier = Modifier.size(38.dp),
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                painter = painterResource(id = R.drawable.ic_cow),
                contentDescription = strings.appName,
                tint = EmeraldPrimary,
                modifier = Modifier.size(22.dp),
              )
            }
          }
          Spacer(modifier = Modifier.width(10.dp))
        }

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
          ) {
            Icon(
              imageVector = Icons.Rounded.CloudOff,
              contentDescription = strings.offlineBadge,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(12.dp),
            )
            Text(
              text = strings.offlineBadge,
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.primary,
              maxLines = 1,
            )
          }
        }
      }

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
      ) {
        // Role Pill
        Surface(
          shape = RoundedCornerShape(50),
          color = if (userRole == UserRole.ADMIN) MintSoft else AmberContainer,
          border = BorderStroke(
            width = 1.dp,
            color = if (userRole == UserRole.ADMIN) MintBorder else WarmAmber.copy(alpha = 0.35f),
          ),
          modifier = Modifier
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(50))
            .clickable(onClick = onOpenSettings)
            .testTag("role_badge_button"),
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
          ) {
            Icon(
              imageVector = if (userRole == UserRole.ADMIN) {
                Icons.Rounded.AdminPanelSettings
              } else {
                Icons.Rounded.Agriculture
              },
              contentDescription = strings.roleDisplay(userRole),
              tint = if (userRole == UserRole.ADMIN) EmeraldDark else WarmAmber,
              modifier = Modifier.size(16.dp),
            )
            Text(
              text = strings.roleDisplay(userRole),
              style = MaterialTheme.typography.labelMedium,
              color = if (userRole == UserRole.ADMIN) EmeraldDark else WarmAmber,
            )
          }
        }

        // Language Toggle Pill (EN / বাংলা)
        Surface(
          shape = RoundedCornerShape(50),
          color = MaterialTheme.colorScheme.primary,
          modifier = Modifier
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(50))
            .clickable(onClick = onToggleLanguage)
            .testTag("language_toggle_button"),
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
          ) {
            Icon(
              imageVector = Icons.Rounded.Translate,
              contentDescription = strings.languageLabel,
              tint = MaterialTheme.colorScheme.onPrimary,
              modifier = Modifier.size(15.dp),
            )
            Text(
              text = language.shortLabel,
              style = MaterialTheme.typography.labelMedium,
              color = MaterialTheme.colorScheme.onPrimary,
            )
          }
        }

        IconButton(
          onClick = onOpenSettings,
          modifier = Modifier
            .minimumInteractiveComponentSize()
            .testTag("settings_button"),
        ) {
          Icon(
            imageVector = Icons.Rounded.Settings,
            contentDescription = strings.settingsTitle,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }
    }
  }
}

@Composable
fun KpiMetricCard(
  title: String,
  value: String,
  icon: ImageVector,
  accentColor: Color,
  containerColor: Color,
  modifier: Modifier = Modifier,
  subtitle: String? = null,
  onClick: (() -> Unit)? = null,
) {
  Card(
    shape = MaterialTheme.shapes.medium,
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = modifier.then(
      if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
    ),
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = title,
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier.weight(1f),
        )
        Surface(
          shape = CircleShape,
          color = containerColor,
          modifier = Modifier.size(32.dp),
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = icon,
              contentDescription = title,
              tint = accentColor,
              modifier = Modifier.size(18.dp),
            )
          }
        }
      }

      Text(
        text = value,
        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )

      if (subtitle != null) {
        Text(
          text = subtitle,
          style = MaterialTheme.typography.bodySmall,
          color = accentColor,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
      }
    }
  }
}

@Composable
fun CattleAvatar(
  photoUri: String?,
  contentDescription: String,
  size: Dp = 60.dp,
  modifier: Modifier = Modifier,
) {
  Surface(
    shape = RoundedCornerShape(14.dp),
    color = MintSoft,
    border = BorderStroke(1.dp, MintBorder),
    modifier = modifier.size(size),
  ) {
    if (!photoUri.isNullOrBlank()) {
      AsyncImage(
        model = photoUri,
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        error = painterResource(id = R.drawable.img_cow_default),
        placeholder = painterResource(id = R.drawable.img_cow_default),
        modifier = Modifier.fillMaxSize(),
      )
    } else {
      Image(
        painter = painterResource(id = R.drawable.img_cow_default),
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize(),
      )
    }
  }
}

@Composable
fun HealthStatusBadge(
  status: String,
  label: String,
  modifier: Modifier = Modifier,
) {
  val (bg, fg) = when (status) {
    "HEALTHY" -> MintSoft to EmeraldDark
    "UNDER_TREATMENT" -> CoralContainer to AlertCoral
    "PREGNANT" -> MilkSkyContainer to MilkSky
    else -> AmberContainer to WarmAmber
  }

  Surface(
    shape = RoundedCornerShape(50),
    color = bg,
    modifier = modifier,
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall,
      color = fg,
      maxLines = 1,
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
    )
  }
}

@Composable
fun EmptyStateCard(
  icon: ImageVector,
  title: String,
  subtitle: String,
  modifier: Modifier = Modifier,
  actionLabel: String? = null,
  onAction: (() -> Unit)? = null,
) {
  Card(
    shape = MaterialTheme.shapes.large,
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    modifier = modifier.fillMaxWidth(),
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(28.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      Surface(
        shape = CircleShape,
        color = MintSoft,
        modifier = Modifier.size(56.dp),
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = icon,
            contentDescription = title,
            tint = EmeraldPrimary,
            modifier = Modifier.size(28.dp),
          )
        }
      }
      Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
      )
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
      if (actionLabel != null && onAction != null) {
        Spacer(modifier = Modifier.height(4.dp))
        Button(onClick = onAction) {
          Text(text = actionLabel)
        }
      }
    }
  }
}

@Composable
fun ConfirmDeleteDialog(
  title: String,
  message: String,
  onConfirm: () -> Unit,
  onDismiss: () -> Unit,
) {
  val strings = LocalAppStrings.current
  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(text = title, style = MaterialTheme.typography.titleLarge) },
    text = { Text(text = message, style = MaterialTheme.typography.bodyMedium) },
    confirmButton = {
      Button(
        onClick = onConfirm,
        colors = ButtonDefaults.buttonColors(containerColor = AlertCoral),
        modifier = Modifier.testTag("confirm_delete_button"),
      ) {
        Text(text = strings.delete)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(text = strings.cancel)
      }
    },
  )
}
