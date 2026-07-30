package com.vikas.cars24sdui.staticscreen

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * Hand-coded twin of the SDUI home screen, same content and layout, no
 * JSON/registry/dispatcher indirection anywhere -- this is the perf
 * baseline for PERF.md. Data and interactive behavior (chip toggle,
 * card-tap feedback, "Call us now" sheet) intentionally match the SDUI
 * version exactly so the comparison isolates SDUI overhead rather than
 * comparing a live page against a dead one.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaticHomeScreen(modifier: Modifier = Modifier) {
    var usedCarsFilter by remember { mutableStateOf("wishlisted") }
    var showCallSheet by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val navigate: (String) -> Unit = { route ->
        scope.launch { snackbarHostState.showSnackbar("Navigate → $route") }
    }

    // Same TTR/TTI marker as SduiScreenHost, for an apples-to-apples PERF.md comparison.
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        (context as? Activity)?.reportFullyDrawn()
        Log.i("SduiPerf", "view_build_complete")
    }

    Scaffold(modifier = modifier, snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).fillMaxSize()) {
            item { StaticHeaderBar(onLocationTap = { navigate("location_picker") }, onSearchTap = { navigate("search") }) }
            item { StaticCategoryTabs(onSelect = { }) }
            item {
                StaticBannerCardRail(
                    title = "Buy car",
                    badgeText = "Upto ₹80,000 off",
                    backgroundColor = Color(0xFF5B4FE9),
                    items = listOf(
                        Triple("🚗", "All used cars") { navigate("listing?filter=all") },
                        Triple("💵", "Budget used cars") { navigate("listing?filter=budget") },
                        Triple("✨", "Premium used cars") { navigate("listing?filter=premium") }
                    )
                )
            }
            item {
                StaticBannerCardRail(
                    title = "Sell your car",
                    badgeText = null,
                    backgroundColor = Color(0xFF0E8F5B),
                    items = listOf(
                        Triple("💸", "Sell your car") { navigate("sell") },
                        Triple("📊", "Check car valuation") { navigate("valuation") },
                        Triple("♻️", "Scrap your car") { navigate("scrap") }
                    )
                )
            }
            item {
                StaticSection(title = "Get loans") {
                    StaticIconGrid(
                        columns = 4,
                        cardBackgroundColor = null,
                        items = listOf(
                            Triple("🚗", "Used car loan") { navigate("loans/used_car") },
                            Triple("🔑", "Loan against car") { navigate("loans/against_car") },
                            Triple("💵", "Personal loan") { navigate("loans/personal") },
                            Triple("💳", "Credit card") { navigate("loans/credit_card") }
                        )
                    )
                }
            }
            item {
                StaticSection(title = "Car check services") {
                    StaticIconGrid(
                        columns = 3,
                        cardBackgroundColor = Color(0xFFF3EFFF),
                        items = listOf(
                            Triple("🔍", "New car PDI") { navigate("services/pdi") },
                            Triple("🩪", "Used car check") { navigate("services/used_car_check") },
                            Triple("📜", "Vehicle history") { navigate("services/history") },
                            Triple("📄", "Check challan") { navigate("services/challan") },
                            Triple("🛡️", "Check car insurance") { navigate("services/insurance") },
                            Triple("⚙️", "Odometer tampering") { navigate("services/odometer") }
                        )
                    )
                }
            }
            item {
                StaticSection(title = "Used cars you'll love", viewAllRoute = "listing/used_cars", onViewAll = navigate) {
                    StaticTextChipRow(
                        options = listOf("wishlisted" to "Wishlisted", "hot_deals" to "Hot deals"),
                        selected = usedCarsFilter,
                        onSelect = { usedCarsFilter = it }
                    )
                    Spacer(Modifier.height(8.dp))
                    if (usedCarsFilter == "wishlisted") {
                        StaticCarCard(
                            title = "2015 Maruti Celerio VXI AMT",
                            subtitle = "54,020 km • Petrol • Auto • White",
                            price = "₹2.14 lakh",
                            priceSubtext = "EMI ₹4,499/mo",
                            tint = Color(0xFF64748B),
                            badges = listOf("Cars24 Assured", "5 day money back", "1 year warranty"),
                            onTap = { navigate("car_details?carId=celerio_2015") }
                        )
                    } else {
                        StaticCarCard(
                            title = "2019 Maruti Swift VXI",
                            subtitle = "38,410 km • Petrol • Manual • Red",
                            price = "₹5.62 lakh",
                            priceSubtext = "EMI ₹11,200/mo",
                            tint = Color(0xFFDC2626),
                            badges = listOf("Cars24 Assured", "Hot deal"),
                            onTap = { navigate("car_details?carId=swift_2019") }
                        )
                    }
                }
            }
            item {
                StaticSection(
                    title = "Manage your vehicle",
                    trailingActionLabel = "+ Add vehicle",
                    onTrailingActionTap = { navigate("vehicle/add") },
                    backgroundColor = Color(0xFF5B4FE9),
                    textColor = Color.White
                ) {
                    StaticIconGrid(
                        columns = 3,
                        cardBackgroundColor = null,
                        textColor = Color.White,
                        items = listOf(
                            Triple("🧾", "Pay challan") { navigate("vehicle/challan") },
                            Triple("🆔", "Recharge FASTag") { navigate("vehicle/fastag") },
                            Triple("🛡️", "Get insurance") { navigate("vehicle/insurance") },
                            Triple("💰", "Cash against car") { navigate("vehicle/cash") },
                            Triple("🛠️", "Road side assistance") { navigate("vehicle/rsa") },
                            Triple("📝", "Get warranty") { navigate("vehicle/warranty") }
                        )
                    )
                }
            }
            item {
                StaticPromoBanner(
                    title = "Add your car to Orbit",
                    subtitle = "Enjoy 3-months Spotify Premium free",
                    badgeText = null,
                    tint = Color(0xFF16A34A),
                    backgroundColor = Color(0xFF0E1F14),
                    ctaLabel = "Add car now",
                    onTap = { navigate("orbit") }
                )
            }
            item {
                StaticSection(title = "1 showroom in your city", viewAllRoute = "showrooms", onViewAll = navigate) {
                    StaticShowroomCard(
                        onViewShowroom = { navigate("showroom_details?showroomId=bestech_square_mall") },
                        onCallUs = { showCallSheet = true }
                    )
                }
            }
            item {
                StaticSection(title = "Trending new cars", viewAllRoute = "listing/new_cars", onViewAll = navigate) {
                    StaticCarRail(
                        items = listOf(
                            Triple("Seltos", "Kia", Color(0xFF2563EB)),
                            Triple("Sonet", "Kia", Color(0xFF9CA3AF)),
                            Triple("Syros", "Kia", Color(0xFF111827))
                        ),
                        onTap = { model -> navigate("new_car_details?modelId=$model") }
                    )
                }
            }
            item {
                StaticPromoBanner(
                    title = "Let us find your match",
                    subtitle = "Answer a few simple questions and get your perfect car match in 60 seconds",
                    badgeText = "Recommended",
                    tint = Color(0xFF7C3AED),
                    backgroundColor = Color(0xFFF3EFFF),
                    textColor = Color(0xFF1A1A1A),
                    ctaLabel = "Find my perfect match",
                    onTap = { navigate("match_finder") }
                )
            }
            item {
                StaticPromoBanner(
                    title = "30 day return",
                    subtitle = "We take it back as easily as we deliver it",
                    badgeText = null,
                    tint = Color(0xFF16A34A),
                    backgroundColor = Color(0xFF0E8F5B),
                    ctaLabel = "Know more",
                    onTap = { navigate("return_policy") }
                )
            }
            item {
                StaticPromoBanner(
                    title = "CRASHFREE INDIA",
                    subtitle = "Control. Judgment. Patience.",
                    badgeText = null,
                    tint = Color(0xFF111827),
                    backgroundColor = Color(0xFF141414),
                    ctaLabel = "Explore now",
                    onTap = { navigate("crashfree_campaign") }
                )
            }
            item {
                StaticFooter()
            }
        }
    }

    if (showCallSheet) {
        ModalBottomSheet(onDismissRequest = { showCallSheet = false }) {
            Column(Modifier.fillMaxWidth().padding(24.dp)) {
                Text("Sheet: call_showroom", style = MaterialTheme.typography.titleMedium)
                Text("showroomId=bestech_square_mall", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun StaticHeaderBar(onLocationTap: () -> Unit, onSearchTap: () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable(onClick = onLocationTap)
            ) {
                Text("📍 Chandigarh  ▾", style = MaterialTheme.typography.titleSmall)
            }
            Spacer(Modifier.weight(1f))
            Box(
                modifier = Modifier.size(32.dp).background(MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("V", color = Color.White, style = MaterialTheme.typography.labelLarge)
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFFF0F0F5))
                .clickable(onClick = onSearchTap)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text("🔍  Search Ertiga", color = Color(0xFF8A8A99), style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun StaticCategoryTabs(onSelect: (String) -> Unit) {
    var selected by remember { mutableStateOf("all") }
    val options = listOf(
        Triple("all", "All", "📱"),
        Triple("buy", "Buy used cars", "🚗"),
        Triple("sell", "Sell your car", "🏷️"),
        Triple("loans", "Loans", "💰"),
        Triple("services", "Chai point", "☕")
    )
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(options, key = { it.first }) { (id, label, icon) ->
            val isSelected = id == selected
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { selected = id; onSelect(id) }.padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFFF0F0F5)),
                    contentAlignment = Alignment.Center
                ) { Text(icon) }
                Spacer(Modifier.height(4.dp))
                Text(label, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
private fun StaticBannerCardRail(
    title: String,
    badgeText: String?,
    backgroundColor: Color,
    items: List<Triple<String, String, () -> Unit>>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(title, color = Color.White, style = MaterialTheme.typography.titleMedium)
            badgeText?.let {
                Spacer(Modifier.width(8.dp))
                Text(
                    it,
                    color = backgroundColor,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.White)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(items.size) { index ->
                val (icon, label, onTap) = items[index]
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(84.dp).clickable(onClick = onTap)
                ) {
                    Box(
                        modifier = Modifier.size(48.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) { Text(icon, style = MaterialTheme.typography.titleMedium) }
                    Spacer(Modifier.height(4.dp))
                    Text(label, color = Color.White, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Composable
private fun StaticSection(
    title: String,
    viewAllRoute: String? = null,
    onViewAll: (String) -> Unit = {},
    trailingActionLabel: String? = null,
    onTrailingActionTap: () -> Unit = {},
    backgroundColor: Color? = null,
    textColor: Color = Color(0xFF1A1A1A),
    content: @Composable ColumnScope.() -> Unit
) {
    var modifier = Modifier.fillMaxWidth()
    backgroundColor?.let { modifier = modifier.background(it) }

    Column(modifier.padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, color = textColor, style = MaterialTheme.typography.titleMedium)
            when {
                trailingActionLabel != null -> Text(
                    trailingActionLabel,
                    color = textColor,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.clickable(onClick = onTrailingActionTap)
                )
                viewAllRoute != null -> Text(
                    "View all",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.clickable { onViewAll(viewAllRoute) }
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        content()
    }
}

@Composable
private fun StaticIconGrid(
    columns: Int,
    cardBackgroundColor: Color?,
    textColor: Color = Color(0xFF1A1A1A),
    items: List<Triple<String, String, () -> Unit>>
) {
    var modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
    cardBackgroundColor?.let {
        modifier = modifier.clip(RoundedCornerShape(16.dp)).background(it).padding(16.dp)
    }
    Column(modifier) {
        items.chunked(columns).forEach { row ->
            Row(Modifier.fillMaxWidth()) {
                row.forEach { (icon, label, onTap) ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f).clickable(onClick = onTap).padding(vertical = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier.size(44.dp).clip(CircleShape).background(Color.Black.copy(alpha = 0.05f)),
                            contentAlignment = Alignment.Center
                        ) { Text(icon) }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            label,
                            color = textColor,
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.Center,
                            maxLines = 2
                        )
                    }
                }
                repeat(columns - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun StaticTextChipRow(options: List<Pair<String, String>>, selected: String, onSelect: (String) -> Unit) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { (id, label) ->
            val isSelected = id == selected
            Text(
                label,
                color = if (isSelected) Color.White else Color(0xFF1A1A1A),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFFF0F0F5))
                    .clickable { onSelect(id) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun StaticCarCard(
    title: String,
    subtitle: String,
    price: String,
    priceSubtext: String,
    tint: Color,
    badges: List<String>,
    onTap: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFFE5E5EA), RoundedCornerShape(12.dp))
            .clickable(onClick = onTap)
            .padding(12.dp)
    ) {
        StaticPlaceholder(tint = tint, modifier = Modifier.size(96.dp).clip(RoundedCornerShape(8.dp)))
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B6B6B))
            Spacer(Modifier.height(4.dp))
            Row {
                Text(price, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.width(6.dp))
                Text(priceSubtext, style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B6B6B))
            }
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                badges.forEach { badge ->
                    Text(
                        badge,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        softWrap = false,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StaticPromoBanner(
    title: String,
    subtitle: String,
    badgeText: String?,
    tint: Color,
    backgroundColor: Color,
    textColor: Color = Color.White,
    ctaLabel: String,
    onTap: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable(onClick = onTap)
    ) {
        StaticPlaceholder(tint = tint, modifier = Modifier.fillMaxWidth().height(140.dp))
        Column(Modifier.padding(16.dp)) {
            badgeText?.let {
                Text(
                    it,
                    color = backgroundColor,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.White)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
                Spacer(Modifier.height(6.dp))
            }
            Text(title, color = textColor, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(subtitle, color = textColor.copy(alpha = 0.85f), style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(12.dp))
            Text(
                ctaLabel,
                color = backgroundColor,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun StaticShowroomCard(onViewShowroom: () -> Unit, onCallUs: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFFE5E5EA), RoundedCornerShape(16.dp))
    ) {
        StaticPlaceholder(tint = Color(0xFF4B5563), modifier = Modifier.fillMaxWidth().height(140.dp))
        Column(Modifier.padding(16.dp)) {
            Text("Bestech Square Mall", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text("Sector 65, Chandigarh", style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B6B6B))
            Row {
                Text("5.7 km away", style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B6B6B))
                Spacer(Modifier.width(8.dp))
                Text("Opens at 11:00 AM", style = MaterialTheme.typography.bodySmall, color = Color(0xFFCC4400))
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Call us now",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp))
                        .clickable(onClick = onCallUs)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
                Text(
                    "View showroom",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable(onClick = onViewShowroom)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun StaticCarRail(items: List<Triple<String, String, Color>>, onTap: (String) -> Unit) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(items.size) { index ->
            val (name, brand, tint) = items[index]
            Column(modifier = Modifier.width(140.dp).clickable { onTap(name.lowercase()) }) {
                StaticPlaceholder(tint = tint, modifier = Modifier.fillMaxWidth().height(100.dp).clip(RoundedCornerShape(12.dp)))
                Spacer(Modifier.height(6.dp))
                Text(name, style = MaterialTheme.typography.titleSmall, maxLines = 1)
                Text(brand, style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B6B6B))
            }
        }
    }
}

@Composable
private fun StaticFooter() {
    Column(
        modifier = Modifier.fillMaxWidth().background(Color(0xFF5B4FE9)).padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "better drives,\nbetter lives",
            color = Color.White,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(12.dp))
        Text("Made with ❤️ in Gurugram", color = Color.White.copy(alpha = 0.85f), style = MaterialTheme.typography.bodyMedium)
    }
}

/** Same Canvas-drawn placeholder approach as the SDUI side's SduiImage, duplicated here deliberately -- this screen has zero dependency on the sdui package. */
@Composable
private fun StaticPlaceholder(tint: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.background(tint)) {
        val w = size.width
        val h = size.height
        val overlay = Color.White.copy(alpha = 0.35f)

        drawCircle(color = overlay, radius = h * 0.12f, center = Offset(w * 0.22f, h * 0.28f))

        val mountains = Path().apply {
            moveTo(0f, h)
            lineTo(w * 0.32f, h * 0.45f)
            lineTo(w * 0.55f, h * 0.7f)
            lineTo(w * 0.72f, h * 0.35f)
            lineTo(w, h)
            close()
        }
        drawPath(mountains, color = overlay)
    }
}
