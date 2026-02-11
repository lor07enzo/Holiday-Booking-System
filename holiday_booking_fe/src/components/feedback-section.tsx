import { MessageSquare, Star } from "lucide-react";
import { cn } from "@/lib/utils";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import type { IFeedback } from "@/types";

interface FeedbackSectionProps {
    feedbacks: IFeedback[];
}

export const FeedbackSection = ({ feedbacks }: FeedbackSectionProps) => {

    const mediaVoto = feedbacks.length > 0
        ? feedbacks.reduce((acc, curr) => acc + curr.score, 0) / feedbacks.length
        : 0;

    return (
        <div className="mt-6 border-t pt-6">
            <div className="flex items-center justify-between mb-4">
                <h3 className="font-semibold flex items-center gap-2 text-2xl">
                    <MessageSquare className="h-5 w-5 text-primary" />
                    Feedback
                </h3>

                {feedbacks.length > 0 && (
                    <div className="flex items-center gap-2 bg-primary/5 px-3 py-1 rounded-full">
                        <div className="flex items-center gap-0.5">
                            {[1, 2, 3, 4, 5].map((star) => (
                                <Star
                                    key={star}
                                    className={cn(
                                        "h-3.5 w-3.5",
                                        star <= Math.round(mediaVoto)
                                            ? "fill-amber-400 text-amber-400"
                                            : "text-muted-foreground/30"
                                    )}
                                />
                            ))}
                        </div>
                        <span className="text-sm font-bold">{mediaVoto.toFixed(1)}</span>
                        <span className="text-xs text-muted-foreground">
                            ({feedbacks.length})
                        </span>
                    </div>
                )}
            </div>

            {feedbacks.length === 0 ? (
                <div className="bg-muted/10 border border-dashed rounded-xl p-8 text-center">
                    <p className="text-sm text-muted-foreground italic">
                        There are no revews for this property yet.
                        Be the first to stay.
                    </p>
                </div>
            ) : (
                <div className="space-y-4 max-h-100 overflow-y-auto pr-2 custom-scrollbar">
                    {feedbacks.map((feedback) => (
                        <div
                            key={feedback.id}
                            className="p-4 rounded-xl border border-border bg-card shadow-sm hover:shadow-md transition-shadow"
                        >
                            <div className="flex items-start justify-between gap-2 mb-3">
                                <div className="flex items-center gap-3">
                                    <Avatar className="h-9 w-9 border">
                                        <AvatarFallback className="text-xs bg-primary/10 text-primary font-bold">
                                            {feedback.user.name[0]}{feedback.user.lastName[0]}
                                        </AvatarFallback>
                                    </Avatar>
                                    <div>
                                        <p className="text-sm font-semibold">
                                            {feedback.user.name} {feedback.user.lastName}
                                        </p>
                                        <p className="text-[10px] text-muted-foreground uppercase tracking-wider font-medium">
                                            Verified stay
                                            {feedback.createdAt && (
                                                <span className="ml-2 normal-case">
                                                    • {new Date(feedback.createdAt).toISOString().split('T')[0]}
                                                </span>
                                            )}
                                        </p>
                                    </div>
                                </div>
                                <div className="flex items-center gap-0.5">
                                    {[1, 2, 3, 4, 5].map((star) => (
                                        <Star
                                            key={star}
                                            className={cn(
                                                "h-3 w-3",
                                                star <= feedback.score
                                                    ? "fill-amber-400 text-amber-400"
                                                    : "text-muted-foreground/30"
                                            )}
                                        />
                                    ))}
                                </div>
                            </div>

                            <h4 className="text-sm font-bold mb-1">{feedback.title}</h4>
                            <p className="text-sm text-muted-foreground leading-relaxed">
                                {feedback.text}
                            </p>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};