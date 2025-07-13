import java.util.*;

public class SimpleRecommender {

    // Sample user-item ratings (user → (item → rating))
    private static Map<String, Map<String, Integer>> userRatings = new HashMap<>();

    public static void main(String[] args) {
        // Step 1: Initialize data
        initializeSampleData();

        // Step 2: Get user input
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter user name (e.g., U1): ");
        String currentUser = scanner.nextLine().trim().toUpperCase();

        if (!userRatings.containsKey(currentUser)) {
            System.out.println("User not found.");
            return;
        }

        // Step 3: Calculate similarity scores
        Map<String, Double> similarityScores = new HashMap<>();
        for (String otherUser : userRatings.keySet()) {
            if (!otherUser.equals(currentUser)) {
                double score = computeSimilarity(currentUser, otherUser);
                similarityScores.put(otherUser, score);
            }
        }

        // Step 4: Find top recommended items
        Map<String, Double> recommendations = getRecommendations(currentUser, similarityScores);

        // Step 5: Display results
        System.out.println("\nTop Recommendations for " + currentUser + ":");
        if (recommendations.isEmpty()) {
            System.out.println("No recommendations found.");
        } else {
            recommendations.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .forEach(entry -> System.out.println("Item: " + entry.getKey() + " | Score: " + String.format("%.2f", entry.getValue())));
        }

        scanner.close();
    }

    // Step 1: Sample hardcoded user ratings
    private static void initializeSampleData() {
        userRatings.put("U1", Map.of("Item1", 5, "Item2", 3, "Item3", 4));
        userRatings.put("U2", Map.of("Item1", 3, "Item2", 4, "Item4", 2));
        userRatings.put("U3", Map.of("Item2", 5, "Item3", 3, "Item4", 4, "Item5", 4));
        userRatings.put("U4", Map.of("Item1", 4, "Item3", 4, "Item4", 3, "Item5", 5));
    }

    // Step 2: Calculate similarity using simple dot-product / cosine-like approach
    private static double computeSimilarity(String userA, String userB) {
        Map<String, Integer> ratingsA = userRatings.get(userA);
        Map<String, Integer> ratingsB = userRatings.get(userB);

        double dotProduct = 0;
        double normA = 0;
        double normB = 0;

        for (String item : ratingsA.keySet()) {
            int ratingA = ratingsA.get(item);
            normA += ratingA * ratingA;

            if (ratingsB.containsKey(item)) {
                int ratingB = ratingsB.get(item);
                dotProduct += ratingA * ratingB;
                normB += ratingB * ratingB;
            }
        }

        if (dotProduct == 0) return 0;

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    // Step 3: Generate item recommendations
    private static Map<String, Double> getRecommendations(String currentUser, Map<String, Double> similarityScores) {
        Map<String, Integer> currentRatings = userRatings.get(currentUser);
        Map<String, Double> recommendations = new HashMap<>();

        for (String otherUser : userRatings.keySet()) {
            if (otherUser.equals(currentUser)) continue;

            double similarity = similarityScores.getOrDefault(otherUser, 0.0);
            if (similarity <= 0) continue;

            for (Map.Entry<String, Integer> entry : userRatings.get(otherUser).entrySet()) {
                String item = entry.getKey();
                int rating = entry.getValue();

                if (!currentRatings.containsKey(item)) {
                    recommendations.put(item, recommendations.getOrDefault(item, 0.0) + similarity * rating);
                }
            }
        }

        return recommendations;
    }
}
