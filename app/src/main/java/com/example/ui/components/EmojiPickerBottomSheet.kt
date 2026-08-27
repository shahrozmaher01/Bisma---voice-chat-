package com.example.ui.components

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class EmojiItem(
    val char: String,
    val keywords: List<String> = emptyList()
)

data class EmojiCategory(
    val id: String,
    val name: String,
    val icon: String,
    val emojis: List<EmojiItem>
)

object EmojiData {
    val smileys = listOf(
        EmojiItem("😀", listOf("grinning", "face", "smile", "happy")),
        EmojiItem("😃", listOf("smiley", "happy", "joy")),
        EmojiItem("😄", listOf("smile", "laugh", "happy")),
        EmojiItem("😁", listOf("beam", "grin", "teeth")),
        EmojiItem("😆", listOf("laughing", "closed", "eyes", "satisfied")),
        EmojiItem("😅", listOf("sweat_smile", "relief", "nervous")),
        EmojiItem("🤣", listOf("rofl", "rolling", "floor", "laughing")),
        EmojiItem("😂", listOf("joy", "tears", "laughing", "cry")),
        EmojiItem("🙂", listOf("slight_smile", "calm")),
        EmojiItem("🙃", listOf("upside_down", "sarcasm", "silly")),
        EmojiItem("😉", listOf("wink", "flirt")),
        EmojiItem("😊", listOf("blush", "proud", "warm")),
        EmojiItem("😇", listOf("angel", "innocent", "halo")),
        EmojiItem("🥰", listOf("love", "hearts", "adore")),
        EmojiItem("😍", listOf("heart_eyes", "love", "crush")),
        EmojiItem("🤩", listOf("star_struck", "excited", "wow")),
        EmojiItem("😘", listOf("kiss", "blow_kiss", "love")),
        EmojiItem("😗", listOf("kissing", "whistle")),
        EmojiItem("😚", listOf("kiss_closed_eyes")),
        EmojiItem("😙", listOf("kiss_smiling_eyes")),
        EmojiItem("😋", listOf("yum", "delicious", "tongue")),
        EmojiItem("😛", listOf("tongue_out", "playful")),
        EmojiItem("😜", listOf("wink_tongue", "crazy")),
        EmojiItem("🤪", listOf("zany", "wild", "party")),
        EmojiItem("😝", listOf("squint_tongue")),
        EmojiItem("🤑", listOf("money_mouth", "rich", "cash")),
        EmojiItem("🤗", listOf("hugs", "warm")),
        EmojiItem("🤭", listOf("giggle", "secret", "oops")),
        EmojiItem("🤫", listOf("shh", "quiet", "silence")),
        EmojiItem("🤔", listOf("thinking", "wonder", "hmm")),
        EmojiItem("🤐", listOf("zipper", "sealed")),
        EmojiItem("🤨", listOf("raised_eyebrow", "suspicious")),
        EmojiItem("😐", listOf("neutral", "poker_face")),
        EmojiItem("😑", listOf("expressionless", "done")),
        EmojiItem("😶", listOf("no_mouth", "silent")),
        EmojiItem("😏", listOf("smirk", "flirt")),
        EmojiItem("😒", listOf("unamused", "annoyed")),
        EmojiItem("🙄", listOf("roll_eyes", "whatever")),
        EmojiItem("😬", listOf("grimace", "awkward")),
        EmojiItem("🤥", listOf("liar", "pinocchio")),
        EmojiItem("😌", listOf("relieved", "peaceful")),
        EmojiItem("😔", listOf("pensive", "sad")),
        EmojiItem("😪", listOf("sleepy", "tired")),
        EmojiItem("🤤", listOf("drool", "yummy")),
        EmojiItem("😴", listOf("sleeping", "zzz")),
        EmojiItem("😷", listOf("mask", "sick")),
        EmojiItem("🤒", listOf("thermometer", "fever")),
        EmojiItem("🤕", listOf("bandage", "hurt")),
        EmojiItem("🤢", listOf("nauseated", "sick")),
        EmojiItem("🤮", listOf("vomit", "barf")),
        EmojiItem("🤧", listOf("sneeze")),
        EmojiItem("🥵", listOf("hot", "heat")),
        EmojiItem("🥶", listOf("cold", "freezing")),
        EmojiItem("🥴", listOf("woozy", "dizzy")),
        EmojiItem("😵", listOf("dizzy_face")),
        EmojiItem("🤯", listOf("mind_blown", "exploding_head")),
        EmojiItem("🤠", listOf("cowboy", "yeehaw")),
        EmojiItem("🥳", listOf("party", "celebrate", "birthday")),
        EmojiItem("😎", listOf("sunglasses", "cool")),
        EmojiItem("🤓", listOf("nerd", "geek")),
        EmojiItem("🧐", listOf("monocle", "curious")),
        EmojiItem("😕", listOf("confused")),
        EmojiItem("😟", listOf("worried")),
        EmojiItem("🙁", listOf("frown")),
        EmojiItem("😮", listOf("open_mouth", "surprised")),
        EmojiItem("😯", listOf("hushed", "shocked")),
        EmojiItem("😲", listOf("astonished")),
        EmojiItem("😳", listOf("flushed", "embarrassed")),
        EmojiItem("🥺", listOf("pleading", "puppy_eyes", "beg")),
        EmojiItem("😦", listOf("frowning_open")),
        EmojiItem("😧", listOf("anguished")),
        EmojiItem("😨", listOf("fearful", "scared")),
        EmojiItem("😰", listOf("anxious", "cold_sweat")),
        EmojiItem("😥", listOf("sad_relieved")),
        EmojiItem("😢", listOf("crying", "tear")),
        EmojiItem("😭", listOf("sob", "crying_loud")),
        EmojiItem("😱", listOf("scream", "shock")),
        EmojiItem("😖", listOf("confounded")),
        EmojiItem("😣", listOf("persevering")),
        EmojiItem("😞", listOf("disappointed")),
        EmojiItem("😓", listOf("sweat", "downcast")),
        EmojiItem("😩", listOf("weary")),
        EmojiItem("😫", listOf("tired")),
        EmojiItem("🥱", listOf("yawning")),
        EmojiItem("😤", listOf("triumph", "huff")),
        EmojiItem("😡", listOf("rage", "pout", "angry")),
        EmojiItem("😠", listOf("angry", "mad")),
        EmojiItem("🤬", listOf("cursing", "swearing")),
        EmojiItem("😈", listOf("devil_smile", "evil")),
        EmojiItem("👿", listOf("devil_angry")),
        EmojiItem("💀", listOf("skull", "dead", "skeleton")),
        EmojiItem("☠️", listOf("crossbones", "danger")),
        EmojiItem("💩", listOf("poop", "poo")),
        EmojiItem("🤡", listOf("clown", "funny")),
        EmojiItem("👹", listOf("ogre", "monster")),
        EmojiItem("👺", listOf("goblin")),
        EmojiItem("👻", listOf("ghost", "boo", "spooky")),
        EmojiItem("👽", listOf("alien", "ufo")),
        EmojiItem("👾", listOf("space_invader", "game")),
        EmojiItem("🤖", listOf("robot", "bot")),
        EmojiItem("😺", listOf("cat_smile")),
        EmojiItem("😸", listOf("cat_grin")),
        EmojiItem("😹", listOf("cat_joy")),
        EmojiItem("😻", listOf("cat_heart_eyes")),
        EmojiItem("😼", listOf("cat_smirk")),
        EmojiItem("😽", listOf("cat_kiss")),
        EmojiItem("🙀", listOf("cat_scream")),
        EmojiItem("😿", listOf("cat_cry")),
        EmojiItem("😾", listOf("cat_pout")),
        EmojiItem("💋", listOf("kiss_mark", "lips")),
        EmojiItem("💌", listOf("love_letter")),
        EmojiItem("💘", listOf("heart_arrow")),
        EmojiItem("💝", listOf("heart_ribbon")),
        EmojiItem("💖", listOf("sparkle_heart")),
        EmojiItem("💗", listOf("growing_heart")),
        EmojiItem("💓", listOf("beating_heart")),
        EmojiItem("💞", listOf("revolving_hearts")),
        EmojiItem("💕", listOf("two_hearts")),
        EmojiItem("💟", listOf("heart_decoration")),
        EmojiItem("❣️", listOf("heart_exclamation")),
        EmojiItem("💔", listOf("broken_heart")),
        EmojiItem("❤️", listOf("red_heart", "love")),
        EmojiItem("🧡", listOf("orange_heart")),
        EmojiItem("💛", listOf("yellow_heart")),
        EmojiItem("💚", listOf("green_heart")),
        EmojiItem("💙", listOf("blue_heart")),
        EmojiItem("💜", listOf("purple_heart")),
        EmojiItem("🤎", listOf("brown_heart")),
        EmojiItem("🖤", listOf("black_heart")),
        EmojiItem("🤍", listOf("white_heart")),
        EmojiItem("💯", listOf("100", "hundred", "perfect")),
        EmojiItem("💢", listOf("anger", "furious")),
        EmojiItem("💥", listOf("boom", "collision", "explode")),
        EmojiItem("💫", listOf("dizzy", "stars")),
        EmojiItem("💦", listOf("sweat_drops", "water")),
        EmojiItem("💨", listOf("dash", "fast", "wind")),
        EmojiItem("💣", listOf("bomb", "boom")),
        EmojiItem("💬", listOf("speech_bubble", "chat")),
        EmojiItem("💭", listOf("thought_bubble", "dream")),
        EmojiItem("💤", listOf("sleep", "zzz"))
    )

    val people = listOf(
        EmojiItem("👋", listOf("wave", "hello", "bye")),
        EmojiItem("🤚", listOf("raised_back_hand")),
        EmojiItem("🖐️", listOf("fingers_splayed")),
        EmojiItem("✋", listOf("high_five", "stop", "hand")),
        EmojiItem("🖖", listOf("vulcan", "spock")),
        EmojiItem("👌", listOf("ok", "perfect")),
        EmojiItem("🤏", listOf("pinching", "little")),
        EmojiItem("✌️", listOf("peace", "victory", "two")),
        EmojiItem("🤞", listOf("crossed_fingers", "luck")),
        EmojiItem("🤟", listOf("love_you_gesture")),
        EmojiItem("🤘", listOf("rock_on", "horns")),
        EmojiItem("🤙", listOf("call_me", "shaka")),
        EmojiItem("👈", listOf("point_left")),
        EmojiItem("👉", listOf("point_right")),
        EmojiItem("👆", listOf("point_up")),
        EmojiItem("👇", listOf("point_down")),
        EmojiItem("☝️", listOf("index_up")),
        EmojiItem("👍", listOf("thumbs_up", "like", "yes", "good")),
        EmojiItem("👎", listOf("thumbs_down", "dislike", "no")),
        EmojiItem("✊", listOf("fist", "power")),
        EmojiItem("👊", listOf("punch", "fist_bump")),
        EmojiItem("🤛", listOf("left_fist")),
        EmojiItem("🤜", listOf("right_fist")),
        EmojiItem("👏", listOf("applause", "clapping", "bravo")),
        EmojiItem("🙌", listOf("raising_hands", "hooray", "praise")),
        EmojiItem("👐", listOf("open_hands")),
        EmojiItem("🤲", listOf("palms_up_together")),
        EmojiItem("🤝", listOf("handshake", "deal", "agree")),
        EmojiItem("🙏", listOf("pray", "please", "thanks", "namaste")),
        EmojiItem("✍️", listOf("writing")),
        EmojiItem("💅", listOf("nails", "glamour")),
        EmojiItem("🤳", listOf("selfie", "camera")),
        EmojiItem("💪", listOf("muscle", "strong", "bicep")),
        EmojiItem("🦵", listOf("leg")),
        EmojiItem("🦶", listOf("foot")),
        EmojiItem("👂", listOf("ear", "listen")),
        EmojiItem("👃", listOf("nose", "smell")),
        EmojiItem("🧠", listOf("brain", "smart")),
        EmojiItem("🫀", listOf("heart_organ")),
        EmojiItem("🫁", listOf("lungs")),
        EmojiItem("🦷", listOf("tooth")),
        EmojiItem("👀", listOf("eyes", "look")),
        EmojiItem("👁️", listOf("eye")),
        EmojiItem("👅", listOf("tongue")),
        EmojiItem("👄", listOf("mouth", "lips")),
        EmojiItem("👶", listOf("baby")),
        EmojiItem("🧒", listOf("child")),
        EmojiItem("👦", listOf("boy")),
        EmojiItem("👧", listOf("girl")),
        EmojiItem("🧑", listOf("person", "adult")),
        EmojiItem("👱", listOf("blond")),
        EmojiItem("👨", listOf("man")),
        EmojiItem("🧔", listOf("beard")),
        EmojiItem("👩", listOf("woman")),
        EmojiItem("🧓", listOf("older_adult")),
        EmojiItem("👴", listOf("grandfather")),
        EmojiItem("👵", listOf("grandmother")),
        EmojiItem("🤴", listOf("prince")),
        EmojiItem("👸", listOf("princess", "queen", "crown")),
        EmojiItem("🧕", listOf("hijab")),
        EmojiItem("🤵", listOf("tuxedo")),
        EmojiItem("👰", listOf("bride")),
        EmojiItem("👼", listOf("baby_angel")),
        EmojiItem("🦸", listOf("superhero")),
        EmojiItem("🦹", listOf("supervillain")),
        EmojiItem("🧙", listOf("mage", "wizard")),
        EmojiItem("🧚", listOf("fairy")),
        EmojiItem("🧛", listOf("vampire")),
        EmojiItem("🧜", listOf("mermaid")),
        EmojiItem("🧝", listOf("elf")),
        EmojiItem("🧞", listOf("genie")),
        EmojiItem("🧟", listOf("zombie")),
        EmojiItem("💃", listOf("dancer", "party")),
        EmojiItem("🕺", listOf("disco_dancer")),
        EmojiItem("🧗", listOf("climber")),
        EmojiItem("🏇", listOf("horse_racing")),
        EmojiItem("🏄", listOf("surfer")),
        EmojiItem("🏊", listOf("swimmer")),
        EmojiItem("⛹️", listOf("basketball_player")),
        EmojiItem("🏋️", listOf("weightlifter")),
        EmojiItem("🚴", listOf("bicyclist")),
        EmojiItem("🤸", listOf("cartwheel")),
        EmojiItem("🤹", listOf("juggling")),
        EmojiItem("🧘", listOf("meditation", "yoga"))
    )

    val animals = listOf(
        EmojiItem("🐶", listOf("dog", "puppy", "pet")),
        EmojiItem("🐱", listOf("cat", "kitten", "pet")),
        EmojiItem("🐭", listOf("mouse")),
        EmojiItem("🐹", listOf("hamster")),
        EmojiItem("🐰", listOf("rabbit", "bunny")),
        EmojiItem("🦊", listOf("fox")),
        EmojiItem("🐻", listOf("bear")),
        EmojiItem("🐼", listOf("panda")),
        EmojiItem("🐻‍❄️", listOf("polar_bear")),
        EmojiItem("🐨", listOf("koala")),
        EmojiItem("🐯", listOf("tiger")),
        EmojiItem("🦁", listOf("lion", "king")),
        EmojiItem("🐮", listOf("cow")),
        EmojiItem("🐷", listOf("pig")),
        EmojiItem("🐸", listOf("frog")),
        EmojiItem("🐵", listOf("monkey")),
        EmojiItem("🙈", listOf("see_no_evil")),
        EmojiItem("🙉", listOf("hear_no_evil")),
        EmojiItem("🙊", listOf("speak_no_evil")),
        EmojiItem("🐔", listOf("chicken")),
        EmojiItem("🐧", listOf("penguin")),
        EmojiItem("🐦", listOf("bird")),
        EmojiItem("🐤", listOf("baby_chick")),
        EmojiItem("🦆", listOf("duck")),
        EmojiItem("🦅", listOf("eagle")),
        EmojiItem("🦉", listOf("owl")),
        EmojiItem("🐺", listOf("wolf")),
        EmojiItem("🐴", listOf("horse")),
        EmojiItem("🦄", listOf("unicorn", "magic")),
        EmojiItem("🐝", listOf("bee", "honey")),
        EmojiItem("🐛", listOf("bug", "caterpillar")),
        EmojiItem("🦋", listOf("butterfly", "beauty")),
        EmojiItem("🐌", listOf("snail")),
        EmojiItem("🐞", listOf("ladybug")),
        EmojiItem("🐜", listOf("ant")),
        EmojiItem("🕷️", listOf("spider")),
        EmojiItem("🦂", listOf("scorpion")),
        EmojiItem("🐢", listOf("turtle")),
        EmojiItem("🐍", listOf("snake")),
        EmojiItem("🐙", listOf("octopus")),
        EmojiItem("🦑", listOf("squid")),
        EmojiItem("🦐", listOf("shrimp")),
        EmojiItem("🦞", listOf("lobster")),
        EmojiItem("🦀", listOf("crab")),
        EmojiItem("🐡", listOf("blowfish")),
        EmojiItem("🐠", listOf("tropical_fish")),
        EmojiItem("🐟", listOf("fish")),
        EmojiItem("🐬", listOf("dolphin")),
        EmojiItem("🐳", listOf("whale")),
        EmojiItem("🦈", listOf("shark")),
        EmojiItem("🐊", listOf("crocodile")),
        EmojiItem("🐅", listOf("tiger_full")),
        EmojiItem("🐆", listOf("leopard")),
        EmojiItem("🦓", listOf("zebra")),
        EmojiItem("🦍", listOf("gorilla")),
        EmojiItem("🐘", listOf("elephant")),
        EmojiItem("🦛", listOf("hippo")),
        EmojiItem("🦏", listOf("rhino")),
        EmojiItem("🐪", listOf("camel")),
        EmojiItem("🦒", listOf("giraffe")),
        EmojiItem("🦘", listOf("kangaroo")),
        EmojiItem("🐕", listOf("dog_sitting")),
        EmojiItem("🐈", listOf("cat_walking")),
        EmojiItem("🦚", listOf("peacock")),
        EmojiItem("🦜", listOf("parrot")),
        EmojiItem("🦩", listOf("flamingo")),
        EmojiItem("🕊️", listOf("dove", "peace")),
        EmojiItem("🐇", listOf("rabbit_sitting")),
        EmojiItem("🦔", listOf("hedgehog")),
        EmojiItem("🐾", listOf("paws", "tracks")),
        EmojiItem("🐉", listOf("dragon")),
        EmojiItem("🌵", listOf("cactus")),
        EmojiItem("🎄", listOf("christmas_tree")),
        EmojiItem("🌲", listOf("evergreen_tree")),
        EmojiItem("🌳", listOf("deciduous_tree")),
        EmojiItem("🌴", listOf("palm_tree", "beach")),
        EmojiItem("🌱", listOf("seedling", "plant")),
        EmojiItem("🌿", listOf("herb")),
        EmojiItem("☘️", listOf("shamrock")),
        EmojiItem("🍀", listOf("four_leaf_clover", "luck")),
        EmojiItem("🍁", listOf("maple_leaf")),
        EmojiItem("🍄", listOf("mushroom")),
        EmojiItem("💐", listOf("bouquet", "flowers")),
        EmojiItem("🌷", listOf("tulip")),
        EmojiItem("🌹", listOf("rose", "love")),
        EmojiItem("🥀", listOf("wilted_flower")),
        EmojiItem("🌺", listOf("hibiscus")),
        EmojiItem("🌸", listOf("cherry_blossom", "sakura")),
        EmojiItem("🌼", listOf("blossom")),
        EmojiItem("🌻", listOf("sunflower")),
        EmojiItem("🌞", listOf("sun_face")),
        EmojiItem("⭐", listOf("star")),
        EmojiItem("🌟", listOf("glowing_star")),
        EmojiItem("✨", listOf("sparkles", "magic")),
        EmojiItem("⚡", listOf("lightning", "zap", "voltage")),
        EmojiItem("🔥", listOf("fire", "flame", "lit", "hot")),
        EmojiItem("🌈", listOf("rainbow")),
        EmojiItem("☀️", listOf("sun", "sunny")),
        EmojiItem("☁️", listOf("cloud")),
        EmojiItem("🌧️", listOf("rain")),
        EmojiItem("❄️", listOf("snowflake", "cold")),
        EmojiItem("💧", listOf("water_drop")),
        EmojiItem("🌊", listOf("ocean_wave", "surf"))
    )

    val food = listOf(
        EmojiItem("🍏", listOf("green_apple")),
        EmojiItem("🍎", listOf("red_apple")),
        EmojiItem("🍐", listOf("pear")),
        EmojiItem("🍊", listOf("tangerine", "orange")),
        EmojiItem("🍋", listOf("lemon")),
        EmojiItem("🍌", listOf("banana")),
        EmojiItem("🍉", listOf("watermelon")),
        EmojiItem("🍇", listOf("grapes")),
        EmojiItem("🍓", listOf("strawberry")),
        EmojiItem("🫐", listOf("blueberries")),
        EmojiItem("🍒", listOf("cherries")),
        EmojiItem("🍑", listOf("peach")),
        EmojiItem("🥭", listOf("mango")),
        EmojiItem("🍍", listOf("pineapple")),
        EmojiItem("🥥", listOf("coconut")),
        EmojiItem("🥝", listOf("kiwi")),
        EmojiItem("🍅", listOf("tomato")),
        EmojiItem("🥑", listOf("avocado")),
        EmojiItem("🥦", listOf("broccoli")),
        EmojiItem("🌶️", listOf("hot_pepper", "spicy")),
        EmojiItem("🌽", listOf("corn")),
        EmojiItem("🥔", listOf("potato")),
        EmojiItem("🍠", listOf("sweet_potato")),
        EmojiItem("🥐", listOf("croissant")),
        EmojiItem("🍞", listOf("bread")),
        EmojiItem("🥖", listOf("baguette")),
        EmojiItem("🥨", listOf("pretzel")),
        EmojiItem("🧀", listOf("cheese")),
        EmojiItem("🍳", listOf("fried_egg")),
        EmojiItem("🥞", listOf("pancakes")),
        EmojiItem("🧇", listOf("waffle")),
        EmojiItem("🥓", listOf("bacon")),
        EmojiItem("🥩", listOf("meat", "steak")),
        EmojiItem("🍗", listOf("poultry_leg", "chicken")),
        EmojiItem("🍖", listOf("meat_on_bone")),
        EmojiItem("🌭", listOf("hotdog")),
        EmojiItem("🍔", listOf("burger", "hamburger")),
        EmojiItem("🍟", listOf("fries", "french_fries")),
        EmojiItem("🍕", listOf("pizza")),
        EmojiItem("🥪", listOf("sandwich")),
        EmojiItem("🌮", listOf("taco")),
        EmojiItem("🌯", listOf("burrito")),
        EmojiItem("🥗", listOf("salad")),
        EmojiItem("🍝", listOf("spaghetti", "pasta")),
        EmojiItem("🍜", listOf("ramen", "noodles")),
        EmojiItem("🍲", listOf("stew", "soup")),
        EmojiItem("🍛", listOf("curry")),
        EmojiItem("🍣", listOf("sushi")),
        EmojiItem("🍱", listOf("bento")),
        EmojiItem("🥟", listOf("dumpling")),
        EmojiItem("🍤", listOf("fried_shrimp")),
        EmojiItem("🍙", listOf("rice_ball")),
        EmojiItem("🍚", listOf("rice")),
        EmojiItem("🍦", listOf("ice_cream")),
        EmojiItem("🍧", listOf("shaved_ice")),
        EmojiItem("🍨", listOf("icecream")),
        EmojiItem("🍩", listOf("doughnut", "donut")),
        EmojiItem("🍪", listOf("cookie")),
        EmojiItem("🎂", listOf("birthday_cake", "celebrate")),
        EmojiItem("🍰", listOf("shortcake", "cake")),
        EmojiItem("🧁", listOf("cupcake")),
        EmojiItem("🥧", listOf("pie")),
        EmojiItem("🍫", listOf("chocolate")),
        EmojiItem("🍬", listOf("candy", "sweet")),
        EmojiItem("🍭", listOf("lollipop")),
        EmojiItem("🍮", listOf("custard", "pudding")),
        EmojiItem("🍯", listOf("honey_pot")),
        EmojiItem("🍼", listOf("baby_bottle")),
        EmojiItem("🥛", listOf("glass_of_milk")),
        EmojiItem("☕", listOf("coffee", "tea", "hot")),
        EmojiItem("🫖", listOf("teapot")),
        EmojiItem("🍵", listOf("teacup", "green_tea")),
        EmojiItem("🧃", listOf("juice")),
        EmojiItem("🥤", listOf("soda", "drink")),
        EmojiItem("🧋", listOf("boba", "bubble_tea")),
        EmojiItem("🍺", listOf("beer", "drinks")),
        EmojiItem("🍻", listOf("cheers", "beers")),
        EmojiItem("🥂", listOf("clinking_glasses", "toast")),
        EmojiItem("🍷", listOf("wine")),
        EmojiItem("🍸", listOf("cocktail")),
        EmojiItem("🍹", listOf("tropical_drink")),
        EmojiItem("🍾", listOf("champagne", "celebrate")),
        EmojiItem("🍿", listOf("popcorn", "movie"))
    )

    val activities = listOf(
        EmojiItem("⚽", listOf("soccer", "football", "ball")),
        EmojiItem("🏀", listOf("basketball")),
        EmojiItem("🏈", listOf("american_football")),
        EmojiItem("⚾", listOf("baseball")),
        EmojiItem("🥎", listOf("softball")),
        EmojiItem("🎾", listOf("tennis")),
        EmojiItem("🏐", listOf("volleyball")),
        EmojiItem("🏉", listOf("rugby")),
        EmojiItem("🎱", listOf("8ball", "pool", "billiards")),
        EmojiItem("🏓", listOf("ping_pong", "table_tennis")),
        EmojiItem("🏸", listOf("badminton")),
        EmojiItem("🥊", listOf("boxing_glove")),
        EmojiItem("🥋", listOf("martial_arts")),
        EmojiItem("🛹", listOf("skateboard")),
        EmojiItem("🛼", listOf("roller_skate")),
        EmojiItem("🎿", listOf("ski")),
        EmojiItem("🏂", listOf("snowboard")),
        EmojiItem("🪂", listOf("parachute")),
        EmojiItem("🏆", listOf("trophy", "winner", "first", "champion")),
        EmojiItem("🥇", listOf("gold_medal", "1st")),
        EmojiItem("🥈", listOf("silver_medal", "2nd")),
        EmojiItem("🥉", listOf("bronze_medal", "3rd")),
        EmojiItem("🏅", listOf("medal")),
        EmojiItem("🎖️", listOf("military_medal")),
        EmojiItem("🎫", listOf("ticket")),
        EmojiItem("🎟️", listOf("admission_tickets")),
        EmojiItem("🎪", listOf("circus")),
        EmojiItem("🎭", listOf("performing_arts", "theater")),
        EmojiItem("🎨", listOf("art", "palette", "painting")),
        EmojiItem("🎬", listOf("clapperboard", "movie")),
        EmojiItem("🎤", listOf("microphone", "sing", "voice", "karaoke")),
        EmojiItem("🎧", listOf("headphones", "music", "audio")),
        EmojiItem("🎼", listOf("musical_score")),
        EmojiItem("🎹", listOf("piano", "keyboard")),
        EmojiItem("🥁", listOf("drum")),
        EmojiItem("🎷", listOf("saxophone")),
        EmojiItem("🎺", listOf("trumpet")),
        EmojiItem("🎸", listOf("guitar", "rock")),
        EmojiItem("🪕", listOf("banjo")),
        EmojiItem("🎻", listOf("violin")),
        EmojiItem("🎲", listOf("dice", "game", "lucky")),
        EmojiItem("♟️", listOf("chess", "pawn")),
        EmojiItem("🎯", listOf("target", "bullseye")),
        EmojiItem("🎳", listOf("bowling")),
        EmojiItem("🎮", listOf("video_game", "play", "controller")),
        EmojiItem("🎰", listOf("slot_machine", "jackpot", "casino")),
        EmojiItem("🧩", listOf("puzzle"))
    )

    val travel = listOf(
        EmojiItem("🚗", listOf("car", "automobile")),
        EmojiItem("🚕", listOf("taxi")),
        EmojiItem("🚙", listOf("suv")),
        EmojiItem("🚌", listOf("bus")),
        EmojiItem("🚎", listOf("trolleybus")),
        EmojiItem("🏎️", listOf("racing_car", "fast")),
        EmojiItem("🚓", listOf("police_car")),
        EmojiItem("🚑", listOf("ambulance")),
        EmojiItem("🚒", listOf("fire_engine")),
        EmojiItem("🚚", listOf("truck")),
        EmojiItem("🏍️", listOf("motorcycle", "bike")),
        EmojiItem("🛵", listOf("scooter")),
        EmojiItem("🚲", listOf("bicycle")),
        EmojiItem("🚨", listOf("police_light", "alert", "siren")),
        EmojiItem("🚁", listOf("helicopter")),
        EmojiItem("✈️", listOf("airplane", "flight", "travel")),
        EmojiItem("🛫", listOf("airplane_departure")),
        EmojiItem("🛬", listOf("airplane_arrival")),
        EmojiItem("🚀", listOf("rocket", "launch", "moon")),
        EmojiItem("🛸", listOf("ufo", "flying_saucer")),
        EmojiItem("🛶", listOf("canoe")),
        EmojiItem("⛵", listOf("sailboat")),
        EmojiItem("🚤", listOf("speedboat")),
        EmojiItem("🛳️", listOf("passenger_ship")),
        EmojiItem("🚢", listOf("ship")),
        EmojiItem("⚓", listOf("anchor")),
        EmojiItem("⛽", listOf("fuel_pump")),
        EmojiItem("🗺️", listOf("world_map")),
        EmojiItem("🗽", listOf("statue_of_liberty")),
        EmojiItem("🗼", listOf("tokyo_tower")),
        EmojiItem("🏰", listOf("castle")),
        EmojiItem("🏯", listOf("japanese_castle")),
        EmojiItem("🏟️", listOf("stadium")),
        EmojiItem("🎡", listOf("ferris_wheel")),
        EmojiItem("🎢", listOf("roller_coaster")),
        EmojiItem("🏖️", listOf("beach")),
        EmojiItem("🏝️", listOf("island")),
        EmojiItem("🏜️", listOf("desert")),
        EmojiItem("🌋", listOf("volcano")),
        EmojiItem("⛰️", listOf("mountain")),
        EmojiItem("🏔️", listOf("snow_mountain")),
        EmojiItem("🗻", listOf("mount_fuji")),
        EmojiItem("🏕️", listOf("camping")),
        EmojiItem("⛺", listOf("tent")),
        EmojiItem("🏠", listOf("house", "home")),
        EmojiItem("🏡", listOf("house_with_garden")),
        EmojiItem("🏢", listOf("office_building")),
        EmojiItem("🏬", listOf("department_store")),
        EmojiItem("🏥", listOf("hospital")),
        EmojiItem("🏦", listOf("bank")),
        EmojiItem("🏨", listOf("hotel")),
        EmojiItem("🏩", listOf("love_hotel")),
        EmojiItem("🏛️", listOf("classical_building")),
        EmojiItem("⛪", listOf("church")),
        EmojiItem("🕌", listOf("mosque")),
        EmojiItem("⛩️", listOf("shinto_shrine")),
        EmojiItem("🕋", listOf("kaaba"))
    )

    val objects = listOf(
        EmojiItem("⌚", listOf("watch", "time")),
        EmojiItem("📱", listOf("phone", "mobile", "iphone")),
        EmojiItem("💻", listOf("laptop", "computer")),
        EmojiItem("🖥️", listOf("desktop")),
        EmojiItem("📷", listOf("camera", "photo")),
        EmojiItem("📸", listOf("camera_flash")),
        EmojiItem("🎥", listOf("movie_camera")),
        EmojiItem("📞", listOf("telephone")),
        EmojiItem("☎️", listOf("phone_call")),
        EmojiItem("📺", listOf("tv", "television")),
        EmojiItem("📻", listOf("radio")),
        EmojiItem("🎙️", listOf("studio_microphone")),
        EmojiItem("⏰", listOf("alarm_clock")),
        EmojiItem("⌛", listOf("hourglass")),
        EmojiItem("🔋", listOf("battery")),
        EmojiItem("💡", listOf("lightbulb", "idea")),
        EmojiItem("🔦", listOf("flashlight")),
        EmojiItem("🕯️", listOf("candle")),
        EmojiItem("💸", listOf("money_wings", "cash")),
        EmojiItem("💵", listOf("dollar_bill")),
        EmojiItem("🪙", listOf("coin", "gold")),
        EmojiItem("💰", listOf("moneybag", "rich")),
        EmojiItem("💳", listOf("credit_card")),
        EmojiItem("💎", listOf("gem", "diamond", "jewel")),
        EmojiItem("⚖️", listOf("scales", "justice")),
        EmojiItem("🧰", listOf("toolbox")),
        EmojiItem("🔧", listOf("wrench")),
        EmojiItem("🔨", listOf("hammer")),
        EmojiItem("🔑", listOf("key")),
        EmojiItem("🗝️", listOf("old_key")),
        EmojiItem("🚪", listOf("door")),
        EmojiItem("🛍️", listOf("shopping_bags")),
        EmojiItem("🛒", listOf("shopping_cart")),
        EmojiItem("🎁", listOf("gift", "present", "box")),
        EmojiItem("🎈", listOf("balloon", "party")),
        EmojiItem("🎀", listOf("ribbon")),
        EmojiItem("🪄", listOf("magic_wand")),
        EmojiItem("🎊", listOf("confetti_ball")),
        EmojiItem("🎉", listOf("party_popper", "celebration")),
        EmojiItem("✉️", listOf("envelope", "mail")),
        EmojiItem("📦", listOf("package", "box")),
        EmojiItem("📜", listOf("scroll")),
        EmojiItem("📝", listOf("memo", "note")),
        EmojiItem("📌", listOf("pushpin")),
        EmojiItem("📍", listOf("round_pushpin", "location")),
        EmojiItem("📎", listOf("paperclip")),
        EmojiItem("✂️", listOf("scissors")),
        EmojiItem("🔒", listOf("lock", "secure")),
        EmojiItem("🔓", listOf("unlock"))
    )

    val symbols = listOf(
        EmojiItem("❤️", listOf("heart", "love", "red")),
        EmojiItem("💔", listOf("broken_heart")),
        EmojiItem("❣️", listOf("heavy_heart_exclamation")),
        EmojiItem("💕", listOf("two_hearts")),
        EmojiItem("💖", listOf("sparkling_heart")),
        EmojiItem("💗", listOf("growing_heart")),
        EmojiItem("💓", listOf("beating_heart")),
        EmojiItem("💞", listOf("revolving_hearts")),
        EmojiItem("💘", listOf("heart_with_arrow")),
        EmojiItem("💝", listOf("heart_with_ribbon")),
        EmojiItem("☮️", listOf("peace_symbol")),
        EmojiItem("✝️", listOf("latin_cross")),
        EmojiItem("☪️", listOf("star_and_crescent")),
        EmojiItem("🕉️", listOf("om")),
        EmojiItem("☸️", listOf("wheel_of_dharma")),
        EmojiItem("✡️", listOf("star_of_david")),
        EmojiItem("☯️", listOf("yin_yang")),
        EmojiItem("☦️", listOf("orthodox_cross")),
        EmojiItem("🛐", listOf("place_of_worship")),
        EmojiItem("🆔", listOf("id")),
        EmojiItem("⚛️", listOf("atom")),
        EmojiItem("🆘", listOf("sos")),
        EmojiItem("❌", listOf("x", "cross", "cancel")),
        EmojiItem("⭕", listOf("circle", "o")),
        EmojiItem("🛑", listOf("stop_sign")),
        EmojiItem("⛔", listOf("no_entry")),
        EmojiItem("🚫", listOf("prohibited")),
        EmojiItem("💯", listOf("100", "score")),
        EmojiItem("💢", listOf("anger_symbol")),
        EmojiItem("♨️", listOf("hot_springs")),
        EmojiItem("❗", listOf("exclamation")),
        EmojiItem("❕", listOf("white_exclamation")),
        EmojiItem("❓", listOf("question")),
        EmojiItem("❔", listOf("white_question")),
        EmojiItem("‼️", listOf("double_exclamation")),
        EmojiItem("⁉️", listOf("interrobang")),
        EmojiItem("⚠️", listOf("warning")),
        EmojiItem("🔱", listOf("trident")),
        EmojiItem("⚜️", listOf("fleur_de_lis")),
        EmojiItem("🔰", listOf("beginner")),
        EmojiItem("♻️", listOf("recycle")),
        EmojiItem("✅", listOf("check", "tick", "done")),
        EmojiItem("🌐", listOf("globe", "internet")),
        EmojiItem("💠", listOf("diamond_shape")),
        EmojiItem("🌀", listOf("cyclone")),
        EmojiItem("💤", listOf("sleep")),
        EmojiItem("🏧", listOf("atm")),
        EmojiItem("🚾", listOf("wc", "restroom")),
        EmojiItem("♿", listOf("wheelchair")),
        EmojiItem("🅿️", listOf("parking")),
        EmojiItem("🚹", listOf("mens")),
        EmojiItem("🚺", listOf("womens")),
        EmojiItem("🚻", listOf("restroom_sign")),
        EmojiItem("📶", listOf("signal_bars")),
        EmojiItem("ℹ️", listOf("info")),
        EmojiItem("🔴", listOf("red_circle")),
        EmojiItem("🟠", listOf("orange_circle")),
        EmojiItem("🟡", listOf("yellow_circle")),
        EmojiItem("🟢", listOf("green_circle")),
        EmojiItem("🔵", listOf("blue_circle")),
        EmojiItem("🟣", listOf("purple_circle")),
        EmojiItem("🟤", listOf("brown_circle")),
        EmojiItem("⚫", listOf("black_circle")),
        EmojiItem("⚪", listOf("white_circle"))
    )

    val flags = listOf(
        EmojiItem("🏁", listOf("chequered_flag")),
        EmojiItem("🚩", listOf("triangular_flag")),
        EmojiItem("🎌", listOf("crossed_flags")),
        EmojiItem("🏴", listOf("black_flag")),
        EmojiItem("🏳️", listOf("white_flag")),
        EmojiItem("🏳️‍🌈", listOf("rainbow_flag", "pride")),
        EmojiItem("🏳️‍⚧️", listOf("transgender_flag")),
        EmojiItem("🏴‍☠️", listOf("pirate_flag")),
        EmojiItem("🇦🇪", listOf("uae", "emirates", "dubai")),
        EmojiItem("🇦🇫", listOf("afghanistan")),
        EmojiItem("🇦🇱", listOf("albania")),
        EmojiItem("🇦🇷", listOf("argentina")),
        EmojiItem("🇦🇹", listOf("austria")),
        EmojiItem("🇦🇺", listOf("australia")),
        EmojiItem("🇦🇿", listOf("azerbaijan")),
        EmojiItem("🇧🇩", listOf("bangladesh")),
        EmojiItem("🇧🇪", listOf("belgium")),
        EmojiItem("🇧🇬", listOf("bulgaria")),
        EmojiItem("🇧🇭", listOf("bahrain")),
        EmojiItem("🇧🇷", listOf("brazil")),
        EmojiItem("🇨🇦", listOf("canada")),
        EmojiItem("🇨🇭", listOf("switzerland")),
        EmojiItem("🇨🇱", listOf("chile")),
        EmojiItem("🇨🇳", listOf("china")),
        EmojiItem("🇨🇴", listOf("colombia")),
        EmojiItem("🇩🇪", listOf("germany")),
        EmojiItem("🇩🇰", listOf("denmark")),
        EmojiItem("🇩🇿", listOf("algeria")),
        EmojiItem("🇪🇬", listOf("egypt")),
        EmojiItem("🇪🇸", listOf("spain")),
        EmojiItem("🇫🇷", listOf("france")),
        EmojiItem("🇬🇧", listOf("uk", "britain", "england")),
        EmojiItem("🇬🇷", listOf("greece")),
        EmojiItem("🇭🇰", listOf("hong_kong")),
        EmojiItem("🇮🇩", listOf("indonesia")),
        EmojiItem("🇮🇪", listOf("ireland")),
        EmojiItem("🇮🇱", listOf("israel")),
        EmojiItem("🇮🇳", listOf("india")),
        EmojiItem("🇮🇶", listOf("iraq")),
        EmojiItem("🇮🇷", listOf("iran")),
        EmojiItem("🇮🇹", listOf("italy")),
        EmojiItem("🇯🇵", listOf("japan")),
        EmojiItem("🇯🇴", listOf("jordan")),
        EmojiItem("🇰🇷", listOf("korea", "south_korea")),
        EmojiItem("🇰🇼", listOf("kuwait")),
        EmojiItem("🇱🇧", listOf("lebanon")),
        EmojiItem("🇲🇽", listOf("mexico")),
        EmojiItem("🇲🇾", listOf("malaysia")),
        EmojiItem("🇳🇬", listOf("nigeria")),
        EmojiItem("🇳🇱", listOf("netherlands")),
        EmojiItem("🇳🇴", listOf("norway")),
        EmojiItem("🇳🇿", listOf("new_zealand")),
        EmojiItem("🇴🇲", listOf("oman")),
        EmojiItem("🇵🇰", listOf("pakistan")),
        EmojiItem("🇵🇭", listOf("philippines")),
        EmojiItem("🇵🇱", listOf("poland")),
        EmojiItem("🇵🇹", listOf("portugal")),
        EmojiItem("🇶🇦", listOf("qatar")),
        EmojiItem("🇷🇴", listOf("romania")),
        EmojiItem("🇷🇺", listOf("russia")),
        EmojiItem("🇸🇦", listOf("saudi_arabia")),
        EmojiItem("🇸🇪", listOf("sweden")),
        EmojiItem("🇸🇬", listOf("singapore")),
        EmojiItem("🇹🇭", listOf("thailand")),
        EmojiItem("🇹🇷", listOf("turkey")),
        EmojiItem("🇺🇦", listOf("ukraine")),
        EmojiItem("🇺🇸", listOf("usa", "america", "united_states")),
        EmojiItem("🇻🇳", listOf("vietnam")),
        EmojiItem("🇿🇦", listOf("south_africa"))
    )

    val categories = listOf(
        EmojiCategory("smileys", "Smileys & Emotion", "😊", smileys),
        EmojiCategory("people", "People & Body", "👋", people),
        EmojiCategory("animals", "Animals & Nature", "🐶", animals),
        EmojiCategory("food", "Food & Drink", "🍔", food),
        EmojiCategory("activities", "Activities", "⚽", activities),
        EmojiCategory("travel", "Travel & Places", "✈️", travel),
        EmojiCategory("objects", "Objects", "💡", objects),
        EmojiCategory("symbols", "Symbols", "❤️", symbols),
        EmojiCategory("flags", "Flags", "🚩", flags)
    )

    val allEmojis = (smileys + people + animals + food + activities + travel + objects + symbols + flags).distinctBy { it.char }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmojiPickerBottomSheet(
    onEmojiSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("bisma_emoji_prefs", Context.MODE_PRIVATE) }
    
    // Load recently used emojis from SharedPreferences
    var recentEmojis by remember {
        val saved = prefs.getString("KEY_RECENT_EMOJIS", "😊,❤️,🔥,👍,🎉,😍,😂,✨") ?: ""
        val list = saved.split(",").filter { it.isNotBlank() }
        mutableStateOf(list)
    }

    fun saveRecentEmoji(emoji: String) {
        val updated = (listOf(emoji) + recentEmojis.filter { it != emoji }).take(32)
        recentEmojis = updated
        prefs.edit().putString("KEY_RECENT_EMOJIS", updated.joinToString(",")).apply()
    }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryIndex by remember { mutableStateOf(0) }

    // Filter emojis based on search query
    val searchResults = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            emptyList()
        } else {
            val query = searchQuery.trim().lowercase()
            EmojiData.allEmojis.filter { item ->
                item.char == query || item.keywords.any { it.contains(query) }
            }
        }
    }

    val displayCategories = remember(recentEmojis) {
        if (recentEmojis.isNotEmpty()) {
            listOf(
                EmojiCategory(
                    id = "recent",
                    name = "Recent",
                    icon = "🕒",
                    emojis = recentEmojis.map { EmojiItem(it, listOf("recent")) }
                )
            ) + EmojiData.categories
        } else {
            EmojiData.categories
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        dragHandle = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .width(42.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.White.copy(alpha = 0.25f))
                )
            }
        },
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 12.dp)
                .padding(bottom = 12.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "😊",
                        fontSize = 20.sp,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    Text(
                        text = "Emojis & Reactions",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Text(
                    text = "Tap to react & send",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search emoji (e.g. happy, love, fire, cat)...", fontSize = 12.sp, color = TextMuted) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = NeonPink, modifier = Modifier.size(18.dp))
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted, modifier = Modifier.size(16.dp))
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonPink,
                    unfocusedBorderColor = SurfaceCardBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = SurfaceCard,
                    unfocusedContainerColor = SurfaceCard
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Category Bar (Hidden when searching)
            if (searchQuery.isBlank()) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    itemsIndexed(displayCategories) { index, cat ->
                        val isSelected = selectedCategoryIndex == index
                        val bgBrush = if (isSelected) PrimaryGradient else null
                        val bgColor = if (isSelected) Color.Transparent else SurfaceCard

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .then(
                                    if (bgBrush != null) Modifier.background(bgBrush)
                                    else Modifier.background(bgColor)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) NeonPink else SurfaceCardBorder,
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable { selectedCategoryIndex = index }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = cat.icon, fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = cat.name,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else TextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            // Grid of Emojis
            val emojisToDisplay = if (searchQuery.isNotBlank()) {
                searchResults
            } else {
                val currentCategory = displayCategories.getOrNull(selectedCategoryIndex) ?: displayCategories.first()
                currentCategory.emojis
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 220.dp, max = 320.dp)
            ) {
                if (emojisToDisplay.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("🔍", fontSize = 32.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("No emojis found for \"$searchQuery\"", color = TextSecondary, fontSize = 13.sp)
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 42.dp),
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        contentPadding = PaddingValues(vertical = 4.dp, horizontal = 2.dp)
                    ) {
                        items(emojisToDisplay) { item ->
                            EmojiCell(
                                emoji = item.char,
                                onClick = {
                                    saveRecentEmoji(item.char)
                                    onEmojiSelected(item.char)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmojiCell(
    emoji: String,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 1.25f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "emoji_scale"
    )

    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                isPressed = true
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        LaunchedEffect(isPressed) {
            if (isPressed) {
                kotlinx.coroutines.delay(120)
                isPressed = false
            }
        }

        Text(
            text = emoji,
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(2.dp)
        )
    }
}
